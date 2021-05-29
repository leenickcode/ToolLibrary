package com.lee.toollibrary.imageloade;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.collection.LruCache;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 图片加载类 http://blog.csdn.net/lmj623565791/article/details/41874561
 * 
 * @author zhy
 * 
 */
@SuppressLint("HandlerLeak")
public class ImageLoader {
	private static ImageLoader mInstance;

	/**
	 * 图片缓存的核心对象
	 */
	private LruCache<String, Bitmap> mLruCache;
	/**
	 * 线程池
	 */
	private ExecutorService          mThreadPool;
	private static final int DEAFULT_THREAD_COUNT = 3;
	/**
	 * 队列的调度方式
	 */
	private Type mType = Type.LIFO;
	/**
	 * 任务队列
	 */
	private LinkedList<Runnable> mTaskQueue;
	/**
	 * 后台轮询线程
	 */
	private Thread mPoolThread;
	private Handler mPoolThreadHandler;
	/**
	 * UI线程中的Handler
	 */
	private Handler mUIHandler;
	private Handler mUIHandler2;

	private Semaphore mSemaphorePoolThreadHandler = new Semaphore(0);
	private Semaphore mSemaphoreThreadPool;

	private boolean isDiskCacheEnable = true;

	private static final String TAG = "ImageLoader";

	private boolean isNeedCallback = false;

	public enum Type {
		FIFO, LIFO;
	}

	private ImageLoader(int threadCount, Type type) {
		init(threadCount, type);
	}

	/**
	 * 初始化
	 * 
	 * @param threadCount
	 * @param type
	 */
	private void init(int threadCount, Type type) {
		initBackThread();

		// 获取我们应用的最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheMemory = maxMemory / 8;
		mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				//返回图片的大小
				return value.getRowBytes() * value.getHeight();
			}

		};

		// 创建线程池
		mThreadPool = Executors.newFixedThreadPool(threadCount);
		mTaskQueue = new LinkedList<Runnable>();
		mType = type;
		mSemaphoreThreadPool = new Semaphore(threadCount);
	}

	/**
	 * 初始化后台轮询线程
	 */
	private void initBackThread() {
		// 后台轮询线程
		mPoolThread = new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				mPoolThreadHandler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						// 线程池去取出一个任务进行执行
						mThreadPool.execute(getTask());
						try {
							mSemaphoreThreadPool.acquire();
						} catch (InterruptedException e) {
						}

						// if(runnable != null){}
					}
				};
				// 释放一个信号量
				mSemaphorePoolThreadHandler.release();
				Looper.loop();
			};
		};

		mPoolThread.start();
	}

	public static ImageLoader getInstance() {
		if (mInstance == null) {
			synchronized (ImageLoader.class) {
				if (mInstance == null) {
					mInstance = new ImageLoader(DEAFULT_THREAD_COUNT, Type.LIFO);
				}
			}
		}
		return mInstance;
	}

	public static ImageLoader getInstance(int threadCount, Type type) {
		if (mInstance == null) {
			synchronized (ImageLoader.class) {
				if (mInstance == null) {
					mInstance = new ImageLoader(threadCount, type);
				}
			}
		}
		return mInstance;
	}

	/**
	 * 根据path为imageview设置图片
	 * @param path
	 * @param imageView
	 */
	@SuppressLint("HandlerLeak")
	public void loadImage(final String path, final ImageView imageView, final boolean isFromNet, final boolean isFromFileFirst) {
		isNeedCallback = false;
		imageView.setTag(path);
		if (mUIHandler == null) {
			mUIHandler = new Handler() {
				public void handleMessage(Message msg) {
					// 获取得到图片，为imageview回调设置图片
					ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
					// 将path与getTag存储路径进行比较
//					LogUtils.dLoger("handleMessage------赋值");
					if (holder.imageView.getTag().toString().equals(holder.path)) {
						if (holder.bitmap != null && !holder.bitmap.isRecycled()) {
							holder.imageView.setImageBitmap(holder.bitmap);
						}
					}
				}
			};
		}
		if (path != null) {
			// 根据path在缓存中获取bitmap
//			Bitmap bm = getBitmapFromLruCache(path);
			Bitmap bm=null;
			if (bm != null) {
				refreashBitmap(path, imageView, bm);
			} else {
//				imageView.setImageResource(R.drawable.image_back);//设置默认占位图
				addTask(buildTask(path, imageView, isFromNet, 2,isFromFileFirst));
			}
		}
	}

	/**
	 *
	 * @param path
	 * @param imageView
	 * @param isFromNet
	 * @param isFromFileFirst
	 * @param isDefImager  是否显示默认图
	 */
	@SuppressLint("HandlerLeak")
	public void loadImage(final String path, final ImageView imageView, final boolean isFromNet, final boolean isFromFileFirst,final boolean isDefImager) {
		isNeedCallback = false;
		imageView.setTag(path);
		if (mUIHandler == null) {
			mUIHandler = new Handler() {
				public void handleMessage(Message msg) {
					// 获取得到图片，为imageview回调设置图片
					ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
					// 将path与getTag存储路径进行比较
					if (holder.imageView.getTag().toString().equals(holder.path)) {
						if (holder.bitmap != null && !holder.bitmap.isRecycled()) {
							holder.imageView.setImageBitmap(holder.bitmap);
						}
					}
				}
			};
		}
		if (path != null) {
			// 根据path在缓存中获取bitmap
			Bitmap bm = getBitmapFromLruCache(path);
			if (bm != null) {
				refreashBitmap(path, imageView, bm);
			} else {
				if (isDefImager){
//					imageView.setImageResource(R.drawable.image_back);//设置默认占位图
				}
				addTask(buildTask(path, imageView, isFromNet, 2,isFromFileFirst));
			}
		}
	}


	public void loadImageOriginAndBlur(final String path, final ImageView imageView, final ImageView imageViewBlur,
			final boolean isFromNet, final boolean isFromFileFirst) {
		imageView.setTag(path);
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				// 获取得到图片，为imageview回调设置图片
				ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
				// 将path与getTag存储路径进行比较
				if (holder.imageView.getTag().toString().equals(holder.path)) {
					if (holder.bitmap != null && !holder.bitmap.isRecycled()) {
						holder.imageView.setImageBitmap(holder.bitmap);
						int scaleRatio = 2;
						int blurRadius = 20;
						Bitmap scaledBitmap = Bitmap.createScaledBitmap(holder.bitmap,
								holder.bitmap.getWidth() / scaleRatio, holder.bitmap.getHeight() / scaleRatio, false);
//						Bitmap blurBitmap = FastBlurUtil.doBlur(scaledBitmap, blurRadius, true);//设置图片模糊的bitmap
//						imageViewBlur.setScaleType(ImageView.ScaleType.FIT_XY);
						imageViewBlur.setImageBitmap(scaledBitmap);
					}
				}
			};
		};
		if (path != null) {
			// 根据path在缓存中获取bitmap
			Bitmap bm = getBitmapFromLruCache(path);
			if (bm != null) {
				Message message = Message.obtain();
				ImgBeanHolder holder = new ImgBeanHolder();
				holder.bitmap = bm;
				holder.path = path;
				holder.imageView = imageView;
				message.obj = holder;
				handler.sendMessage(message);
			} else {
				addTask(new Runnable() {
					@Override
					public void run() {
						Bitmap bm = null;
						if (isFromNet) {
							if (isFromFileFirst) {
								File file = getDiskCacheDir(imageView.getContext(), md5(path));
								if (file.exists())// 如果在缓存文件中发现
								{
//									Log.e(TAG, "find image :" + path + " in disk cache .");
									bm = loadImageFromLocal(file.getAbsolutePath(), imageView, 1);
								} else {
									if (isDiskCacheEnable)// 检测是否开启硬盘缓存
									{
										boolean downloadState = DownloadImgUtils.downloadImgByUrl(path, file);
										if (downloadState)// 如果下载成功
										{
//											Log.e(TAG, "download image :" + path + " to disk cache . path is "
//													+ file.getAbsolutePath());
											bm = loadImageFromLocal(file.getAbsolutePath(), imageView, 1);
										} else {
											Log.i("aasdas",
													"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
											bm = DownloadImgUtils.downloadImgByUrl(path, imageView);
										}
										// bm = loadImageFromUrl(path);
									} else
									// 直接从网络加载
									{
//										Log.e(TAG, "load image :" + path + " to memory.");
										bm = loadImageFromUrl(path);
									}

								}
							}else {
								bm = DownloadImgUtils.downloadImgByUrl(path, imageView);
							}
						} else {
							bm = loadImageFromLocal(path, imageView, 1);
						}
						// 3、把图片加入到缓存
						addBitmapToLruCache(path, bm);

						Message message = Message.obtain();
						ImgBeanHolder holder = new ImgBeanHolder();
						holder.bitmap = bm;
						holder.path = path;
						holder.imageView = imageView;
						message.obj = holder;
						handler.sendMessage(message);

						mSemaphoreThreadPool.release();
					}

				});
			}
		}
	}

	@SuppressLint("HandlerLeak")
	public void loadImageSmall(final String path, final ImageView imageView, final boolean isFromNet, final boolean isFromFileFirst) {
		isNeedCallback = false;
		imageView.setTag(path);
		if (mUIHandler == null) {
			mUIHandler = new Handler() {
				public void handleMessage(Message msg) {
					// 获取得到图片，为imageview回调设置图片
					ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
					// 将path与getTag存储路径进行比较
					if (holder.imageView.getTag().toString().equals(holder.path)) {
						if (holder.bitmap != null && !holder.bitmap.isRecycled()) {
							holder.imageView.setImageBitmap(holder.bitmap);
						}
					}
				};
			};
		}

		if (path != null) {
			// 根据path在缓存中获取bitmap
			Bitmap bm = getBitmapFromLruCache(path);
			if (bm != null) {
				refreashBitmap(path, imageView, bm);
			} else {
				addTask(buildTask(path, imageView, isFromNet, 1,isFromFileFirst));
			}
		}
	}

	@SuppressLint("HandlerLeak")
	public void loadImageBig(final String path, final ImageView imageView, final boolean isFromNet, final boolean isFromFileFirst) {
		isNeedCallback = false;
		imageView.setTag(path);
		if (mUIHandler == null) {
			mUIHandler = new Handler() {
				public void handleMessage(Message msg) {
					// 获取得到图片，为imageview回调设置图片
					ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
					// 将path与getTag存储路径进行比较
					if (holder.imageView.getTag().toString().equals(holder.path)) {
						if (holder.bitmap != null && !holder.bitmap.isRecycled()) {
							holder.imageView.setImageBitmap(holder.bitmap);
						}
					}
				};
			};
		}

		if (path != null) {
			// 根据path在缓存中获取bitmap
			Bitmap bm = getBitmapFromLruCache(path);
			if (bm != null) {
				refreashBitmap(path, imageView, bm);
			} else {
				addTask(buildTask(path, imageView, isFromNet, 3,isFromFileFirst));
			}
		}
	}

	public static Bitmap loadImageFromUrl(String url) {
		URL m;
		InputStream i = null;
		BufferedInputStream bis = null;
		ByteArrayOutputStream out = null;
		try {
			m = new URL(url);
			i = (InputStream) m.getContent();
			bis = new BufferedInputStream(i, 1024 * 8);
			out = new ByteArrayOutputStream();
			int len = 0;
			byte[] buffer = new byte[1024];
			while ((len = bis.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			out.close();
			bis.close();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] data = out.toByteArray();
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		// Drawable d = Drawable.createFromStream(i, "src");
		return bitmap;
	}

	public void loadImageRefresh(final String path, final ImageView imageView, final boolean isFromNet, final boolean isFromFileFirst) {
		isNeedCallback = false;
		imageView.setTag(path);
		if (mUIHandler == null) {
			mUIHandler = new Handler() {
				public void handleMessage(Message msg) {
					// 获取得到图片，为imageview回调设置图片
					ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
					Bitmap bm = holder.bitmap;
					ImageView imageview = holder.imageView;
					String path = holder.path;
					// 将path与getTag存储路径进行比较
					if (imageview.getTag().toString().equals(path)) {
						if (bm != null && !bm.isRecycled()) {
							imageview.setImageBitmap(bm);
						}
					}
				};
			};
		}
		if (path != null) {
			// mLruCache.get(path).recycle();
			mLruCache.remove(path);
			addTask(buildTask(path, imageView, isFromNet, 2,isFromFileFirst));
		}
	}

	/**
	 * 根据path为imageview设置图片
	 * 
	 * @param path
	 * @param imageView
	 */
	@SuppressLint("HandlerLeak")
	public void loadImage(final String path, final ImageView imageView, final boolean isFromNet,
			final OnImageLoadCallBack callBack, final boolean isFromFileFirst) {
		isNeedCallback = true;
		imageView.setTag(path);
		if (mUIHandler2 == null) {
			mUIHandler2 = new Handler() {
				public void handleMessage(Message msg) {
					// 获取得到图片，为imageview回调设置图片
					ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
					// 将path与getTag存储路径进行比较
					if (holder.imageView.getTag().toString().equals(holder.path)) {
						if (holder.bitmap != null && !holder.bitmap.isRecycled()) {
							holder.imageView.setImageBitmap(holder.bitmap);
						}
						callBack.onCallBack();
					}
				};
			};
		}

		if (path != null) {
			// 根据path在缓存中获取bitmap
			Bitmap bm = getBitmapFromLruCache(path);

			if (bm != null) {
				refreashBitmap(path, imageView, bm);
			} else {
				addTask(buildTask(path, imageView, isFromNet, 2,isFromFileFirst));
			}
		}

	}

	@SuppressLint("HandlerLeak")
	public void loadImageRefresh(final String path, final ImageView imageView, final boolean isFromNet,
			final OnImageLoadCallBack callBack, final boolean isFromFileFirst) {
		isNeedCallback = true;
		imageView.setTag(path);
		if (mUIHandler2 == null) {
			mUIHandler2 = new Handler() {
				public void handleMessage(Message msg) {
					// 获取得到图片，为imageview回调设置图片
					ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
					// 将path与getTag存储路径进行比较
					if (holder.imageView.getTag().toString().equals(holder.path)) {
						if (holder.bitmap != null && !holder.bitmap.isRecycled()) {
							holder.imageView.setImageBitmap(holder.bitmap);
						}
						callBack.onCallBack();
					}
				};
			};
		}

		if (path != null) {
			// mLruCache.get(path).recycle();
			mLruCache.remove(path);
			addTask(buildTask(path, imageView, isFromNet, 2,isFromFileFirst));
		}
		// if (path != null) {
		// // 根据path在缓存中获取bitmap
		// Bitmap bm = getBitmapFromLruCache(path);
		//
		// if (bm != null) {
		// refreashBitmap(path, imageView, bm);
		// } else {
		// addTask(buildTask(path, imageView, isFromNet, 2));
		// }
		// }
	}

	@SuppressLint("HandlerLeak")
	public void loadImageWH(final String path, final ImageView imageView, final boolean isFromNet, final int width,
			final int height, final OnImageLoadCallBack callBack, final boolean isFromFileFirst) {
		isNeedCallback = true;
		imageView.setTag(path);
		if (mUIHandler2 == null) {
			mUIHandler2 = new Handler() {
				public void handleMessage(Message msg) {
					// 获取得到图片，为imageview回调设置图片
					ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
					// 将path与getTag存储路径进行比较
					if (holder.imageView.getTag().toString().equals(holder.path)) {
						if (holder.bitmap != null && !holder.bitmap.isRecycled()) {
							holder.imageView.setImageBitmap(holder.bitmap);
						}
						callBack.onCallBack();
					}
				};
			};
		}
		if (path != null) {
			mLruCache.remove(path);
			addTask(buildTaskWH(path, imageView, isFromNet, 3, width, height,isFromFileFirst));
		}
	}

	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float scaleWidht, scaleHeight, x, y;
		Matrix matrix = new Matrix();
		if (width > height) {
			scaleWidht = ((float) h / (float) height);
			scaleHeight = ((float) h / (float) height);
			x = (width - w * height / h) / 2;// 获取bitmap源文件中x做表需要偏移的像数大小
			y = 0;
		} else if (width < height) {
			scaleWidht = ((float) w / (float) width);
			scaleHeight = ((float) w / (float) width);
			x = 0;
			y = (height - h * width / w) / 2;// 获取bitmap源文件中y做表需要偏移的像数大小
		} else {
			scaleWidht = ((float) w / (float) width);
			scaleHeight = ((float) w / (float) width);
			x = 0;
			y = 0;
		}
		matrix.postScale(scaleWidht, scaleHeight);
		try {
			bitmap = Bitmap.createBitmap(bitmap, (int) x, (int) y, (int) (width - x), (int) (height - y), matrix, true);// createBitmap()方法中定义的参数x+width要小于或等于bitmap.getWidth()，y+height要小于或等于bitmap.getHeight()
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	public static Bitmap zoomBitmapWH(Bitmap bitmap, int w, int h) {
		try {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			float scale;
			Matrix matrix = new Matrix();
			if (w / width > h / height) {
				scale = (float) w / (float) width;
			} else if (w / width < h / height) {
				scale = (float) h / (float) height;
			} else {
				scale = (float) w / (float) width;
			}
			matrix.postScale(scale, scale);
			try {
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);// createBitmap()方法中定义的参数x+width要小于或等于bitmap.getWidth()，y+height要小于或等于bitmap.getHeight()
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	@SuppressLint("HandlerLeak")
	public void loadImageRefreshBig(final String path, final ImageView imageView, final boolean isFromNet,
			final OnImageLoadCallBack callBack, final boolean isFromFileFirst) {
		isNeedCallback = true;
		imageView.setTag(path);
		if (mUIHandler2 == null) {
			mUIHandler2 = new Handler() {
				public void handleMessage(Message msg) {
					// 获取得到图片，为imageview回调设置图片
					ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
					// 将path与getTag存储路径进行比较
					if (holder.imageView.getTag().toString().equals(holder.path)) {
						if (holder.bitmap != null && !holder.bitmap.isRecycled()) {
							holder.imageView.setImageBitmap(holder.bitmap);
						}
						callBack.onCallBack();
					}
				};
			};
		}

		if (path != null) {
			// mLruCache.get(path).recycle();
			mLruCache.remove(path);
			addTask(buildTask(path, imageView, isFromNet, 3,isFromFileFirst));
		}
		// if (path != null) {
		// // 根据path在缓存中获取bitmap
		// Bitmap bm = getBitmapFromLruCache(path);
		//
		// if (bm != null) {
		// refreashBitmap(path, imageView, bm);
		// } else {
		// addTask(buildTask(path, imageView, isFromNet, 2));
		// }
		// }

	}

	public interface OnImageLoadCallBack {
		public void onCallBack();
	}

	public void setOnCallBack(OnImageLoadCallBack callBack) {
	}

	/**
	 * 根据传入的参数，新建一个任务
	 * 
	 * @param path
	 * @param imageView
	 * @param isFromNet
	 * @return
	 */
	private Runnable buildTask(final String path, final ImageView imageView, final boolean isFromNet,
			final int isSmall, final boolean isFromFileFirst) {
		return new Runnable() {
			@Override
			public void run() {
				Bitmap bm = null;
				if (isFromNet) {
					if (isFromFileFirst) {
						File file = getDiskCacheDir(imageView.getContext(), md5(path));
						if (file.exists())// 如果在缓存文件中发现
						{
//							Log.e(TAG, "find image :" + path + " in disk cache .");
							bm = loadImageFromLocal(file.getAbsolutePath(), imageView, isSmall);
						} else {
							if (isDiskCacheEnable)// 检测是否开启硬盘缓存
							{
								boolean downloadState = DownloadImgUtils.downloadImgByUrl(path, file);
								if (downloadState)// 如果下载成功
								{
//									Log.e(TAG, "download image :" + path + " to disk cache . path is "
//											+ file.getAbsolutePath());
									bm = loadImageFromLocal(file.getAbsolutePath(), imageView, isSmall);
								} else {
									bm = DownloadImgUtils.downloadImgByUrl(path, imageView);
								}
								// bm = loadImageFromUrl(path);
							} else
							// 直接从网络加载
							{
//								Log.e(TAG, "load image :" + path + " to memory.");
								bm = loadImageFromUrl(path);
							}

						}
					}else {
						bm = DownloadImgUtils.downloadImgByUrl(path, imageView);
						System.out.println("bm.getWidth" + bm.getWidth() + ", bm.getHeight = " + bm.getHeight());
					}
				} else {
					bm = loadImageFromLocal(path, imageView, isSmall);
				}
				// 3、把图片加入到缓存
				addBitmapToLruCache(path, bm);
				refreashBitmap(path, imageView, bm);
				mSemaphoreThreadPool.release();
			}

		};
	}

	private Runnable buildTaskWH(final String path, final ImageView imageView, final boolean isFromNet,
			final int isSmall, final int width, final int height, final boolean isFromFileFirst) {
		return new Runnable() {
			@Override
			public void run() {
				Bitmap bm = null;
				if (isFromNet) {
					if (isFromFileFirst) {
						File file = getDiskCacheDir(imageView.getContext(), md5(path));
						if (file.exists())// 如果在缓存文件中发现
						{
//							Log.e(TAG, "find image :" + path + " in disk cache .");
							bm = loadImageFromLocalWH(file.getAbsolutePath(), imageView, isSmall, width, height);
						} else {
							if (isDiskCacheEnable)// 检测是否开启硬盘缓存
							{
								boolean downloadState = DownloadImgUtils.downloadImgByUrl(path, file);
									if (downloadState)// 如果下载成功
									{
//									Log.e(TAG, "download image :" + path + " to disk cache . path is "
//											+ file.getAbsolutePath());
									bm = loadImageFromLocalWH(file.getAbsolutePath(), imageView, isSmall, width, height);
								} else {
									Log.i("aasdas",
											"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
									bm = DownloadImgUtils.downloadImgByUrl(path, imageView);
								}
								// bm = loadImageFromUrl(path);
							} else
							// 直接从网络加载
							{
//								Log.e(TAG, "load image :" + path + " to memory.");
								bm = loadImageFromUrl(path);
							}

						}
					}else {
						bm = DownloadImgUtils.downloadImgByUrl(path, imageView);
						System.out.println("bm.getWidth" + bm.getWidth() + ", bm.getHeight = " + bm.getHeight());
					}
				} else {
					bm = loadImageFromLocalWH(path, imageView, isSmall, width, height);
				}
				// 3、把图片加入到缓存
				 addBitmapToLruCache(path, bm);
				refreashBitmap(path, imageView, bm);
				mSemaphoreThreadPool.release();
			}

		};
	}

    /**
     * 获取本地图片
     * @param path  文件uri
     * @param imageView
     * @param isSmall 压缩比例
     * @return  图片对象
     */
	public Bitmap loadImageFromLocal(final String path, final ImageView imageView, final int isSmall) {
		Bitmap bm;
		// 加载图片
		// 图片的压缩
		// 1、获得图片需要显示的大小
		ImageSizeUtil.ImageSize imageSize = ImageSizeUtil.getImageViewSize(imageView);
//		System.out.println("imageSize = " + imageSize);
		bm = decodeSampledBitmapFromPath(path, imageSize.width, imageSize.height, isSmall);
		// 2、压缩图片
		return bm;
	}

	public Bitmap loadImageFromLocalWH(final String path, final ImageView imageView, final int isSmall, int width,
			int height) {
		Bitmap bm;
		bm = decodeSampledBitmapFromPathWH(path, width, height, isSmall);
		bm = zoomBitmapWH(bm, width, height);
		// 2、压缩图片
		return bm;
	}

	/**
	 * 从任务队列取出一个方法
	 * 
	 * @return
	 */
	private synchronized Runnable getTask() {
		if (mType == Type.FIFO) {
			return mTaskQueue.removeFirst();
		} else if (mType == Type.LIFO) {
			try {
				// Runnable run = mTaskQueue.getLast();
				return mTaskQueue.removeLast();
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * 利用签名辅助类，将字符串字节数组
	 * 
	 * @param str
	 * @return
	 */
	public String md5(String str) {
		byte[] digest = null;
		try {
			MessageDigest md = MessageDigest.getInstance("md5");
			digest = md.digest(str.getBytes());
			return bytes2hex02(digest);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 方式二
	 * 
	 * @param bytes
	 * @return
	 */
	public String bytes2hex02(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		String tmp = null;
		for (byte b : bytes) {
			// 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
			tmp = Integer.toHexString(0xFF & b);
			if (tmp.length() == 1)// 每个字节8为，转为16进制标志，2个16进制位
			{
				tmp = "0" + tmp;
			}
			sb.append(tmp);
		}

		return sb.toString();

	}

	private void refreashBitmap(final String path, final ImageView imageView, Bitmap bm) {
		Message message = Message.obtain();
		ImgBeanHolder holder = new ImgBeanHolder();
		holder.bitmap = bm;
		holder.path = path;
		holder.imageView = imageView;
		message.obj = holder;
//		LogUtils.dLoger("refreashBitmap");
		if (isNeedCallback) {
			mUIHandler2.sendMessage(message);
		} else {
			mUIHandler.sendMessage(message);
		}
	}

	/**
	 * 
	 * 将图片加入LruCache
	 * 
	 * @param path
	 * @param bm
	 */
	protected void addBitmapToLruCache(String path, Bitmap bm) {
		if (getBitmapFromLruCache(path) == null) {
			if (bm != null)
				mLruCache.put(path, bm);
		}
	}

	/**
	 * 根据图片需要显示的宽和高对图片进行压缩
	 * 
	 * @param path
	 * @param width 需要显示的宽
	 * @param height 需要显示的高
	 * @return
	 */
	protected Bitmap decodeSampledBitmapFromPath(String path, int width, int height, final int isSmall) {
		// 获得图片的宽和高，并不把图片加载到内存中
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inJustDecodeBounds = true;
		// BitmapFactory.decodeFile(path, options);
		if (isSmall == 1) {
			options.inSampleSize = ImageSizeUtil.caculateInSampleSizeSmall(options, width, height);
		} else if (isSmall == 2) {
			options.inSampleSize = ImageSizeUtil.caculateInSampleSize(options, width, height);
		} else if (isSmall == 3) {
			options.inSampleSize = ImageSizeUtil.caculateInSampleSizeBig(options, width, height);
		}
		// 使用获得到的InSampleSize再次解析图片
		options.inJustDecodeBounds = false;

		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeFile(path, options);
			// ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
			// bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			// // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			// int op = 100;
			// int size = 100;
			// if (isSmall) {
			// size =100;
			// op = 50;
			// } else {
			// size = 80;
			// op = 100;
			// }
			// while (baos.toByteArray().length / 1024 > size) {
			// op -= 24;// 每次都减少10
			// baos.reset();// 重置baos即清空baos
			// // 这里压缩options%，把压缩后的数据存放到baos中
			// if (op < 0) {
			// op = 10;
			// bitmap.compress(Bitmap.CompressFormat.JPEG, op, baos);
			// break;
			// } else {
			// bitmap.compress(Bitmap.CompressFormat.JPEG, op, baos);
			// }
			// }
			// // 把压缩后的数据baos存放到ByteArrayInputStream中
			// ByteArrayInputStream isBm = new
			// ByteArrayInputStream(baos.toByteArray());
			// // 把ByteArrayInputStream数据生成图片
			// bitmap = BitmapFactory.decodeStream(isBm, null, null);
			// ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
			// bitmap.compress(Bitmap.CompressFormat.PNG, 10, baos);
			// ByteArrayInputStream isBm = new
			// ByteArrayInputStream(baos.toByteArray());
			// // 把ByteArrayInputStream数据生成图片
			// bitmap = BitmapFactory.decodeStream(isBm, null, null);
			// try {
			// baos.reset();
			// isBm.close();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
		} catch (Error e) {
			System.gc();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);

			options.inSampleSize = 20;

			// 使用获得到的InSampleSize再次解析图片
			options.inJustDecodeBounds = false;
			try {
				bitmap = BitmapFactory.decodeFile(path, options);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
				bitmap.compress(Bitmap.CompressFormat.PNG, 10, baos);
				ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
				// 把ByteArrayInputStream数据生成图片
				bitmap = BitmapFactory.decodeStream(isBm, null, null);
			} catch (Error e2) {
				e2.printStackTrace();
			}
		} catch (Exception e) {
			System.gc();
		}
		return bitmap;
	}

	protected Bitmap decodeSampledBitmapFromPathWH(String path, int width, int height, final int isSmall) {
		// 获得图片的宽和高，并不把图片加载到内存中
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inPurgeable = true;
		options.inInputShareable = true;
		// BitmapFactory.decodeFile(path, options);
		if (isSmall == 1) {
			options.inSampleSize = 8;
		} else if (isSmall == 2) {
			options.inSampleSize = 2;
		} else if (isSmall == 3) {
			options.inSampleSize = 1;
		}
		// 使用获得到的InSampleSize再次解析图片
		options.inJustDecodeBounds = false;

		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeFile(path, options);
		} catch (Error e) {
			System.gc();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);

			options.inSampleSize = 20;

			// 使用获得到的InSampleSize再次解析图片
			options.inJustDecodeBounds = false;
			try {
				bitmap = BitmapFactory.decodeFile(path, options);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
				bitmap.compress(Bitmap.CompressFormat.PNG, 10, baos);
				ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
				// 把ByteArrayInputStream数据生成图片
				bitmap = BitmapFactory.decodeStream(isBm, null, null);
			} catch (Error e2) {
				e2.printStackTrace();
			}
		} catch (Exception e) {
			System.gc();
		}
		return bitmap;
	}

	private synchronized void addTask(Runnable runnable) {
		mTaskQueue.add(runnable);
		// if(mPoolThreadHandler==null)wait();
		try {
			if (mPoolThreadHandler == null)
				mSemaphorePoolThreadHandler.acquire();
		} catch (InterruptedException e) {
		}
		mPoolThreadHandler.sendEmptyMessage(0x110);
	}

	/**
	 * 获得缓存图片的地址
	 * 
	 * @param context
	 * @param uniqueName
	 * @return
	 */
	public File getDiskCacheDir(Context context, String uniqueName) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			cachePath = context.getExternalCacheDir().getAbsolutePath();
		} else {
			cachePath = context.getCacheDir().getAbsolutePath();
		}
		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * 根据path在缓存中获取bitmap
	 * 
	 * @param key
	 * @return
	 */
	private Bitmap getBitmapFromLruCache(String key) {
		return mLruCache.get(key);
	}

	private class ImgBeanHolder {
		Bitmap bitmap;
		ImageView imageView;
		String path;
	}

	public static void recycleLruCache(String key) {
		if (mInstance != null && key != null) {
			Bitmap bitmap = mInstance.mLruCache.get(key);
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
				mInstance.mLruCache.remove(key);
			}
		}
	}
}
