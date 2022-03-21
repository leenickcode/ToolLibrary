package com.example.camera2demo.java;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.camera2demo.R;
import com.example.camera2demo.databinding.FragmentCameraBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeoutException;

/**
 * Created by nicklee on 2022/3/21.
 *
 * @author nicklee
 */

public class CameraFragment extends Fragment {
    private static final String TAG = "CameraFragment";
    CameraManager cameraManager;
    String frontCameraId = "0";
    CameraDevice cameraDevice;

    private CameraCaptureSession mSession;
    /**
     * 用于接受预览数据的surface
     */
    private Surface previewDataSurface;


    CameraCharacteristics characteristics;
    /**
     * 图片名
     */
    String imageKey;
    /**
     * image 缓存大小
     */
    private static final int IMAGE_BUFFER_SIZE = 3;

    private HandlerThread imageReaderThread = new HandlerThread("imageReaderThread");

    private Handler imageReaderHandler;
    private HandlerThread cameraThread = new HandlerThread("CameraThread");

    private Handler cameraHandler;

    ImageReader imageReader;
    ArrayBlockingQueue<Image> imageQueue;
    private File mFile;
    FragmentCameraBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageReaderThread.start();
        imageReaderHandler = new Handler(imageReaderThread.getLooper());
        cameraThread.start();
        cameraHandler = new Handler(cameraThread.getLooper());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentCameraBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.captureButton.setOnClickListener(v -> {
            takePhoto();
        });
//        OrientationEventListener mOrientationListener = new OrientationEventListener(requireContext(),
//                SensorManager.SENSOR_DELAY_NORMAL) {
//
//            @Override
//            public void onOrientationChanged(int orientation) {
//
//                Log.d(TAG, "onOrientationChanged: "+orientation);
//            }
//        };

//        if (mOrientationListener.canDetectOrientation()) {
//
//            Log.d(TAG, "onViewCreated: Can detect orientation");
//            mOrientationListener.enable();
//        } else {
//
//            Log.d(TAG, "onViewCreated: Cannot detect orientation");
//            mOrientationListener.disable();
//        }
        binding.viewFinder.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {

                Log.d(TAG, "surfaceCreated: ");
                previewDataSurface = holder.getSurface();
                openCamera();
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

                Log.d(TAG, "surfaceChanged: " + width + "--" + height);
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

            }
        });
    }


    private Runnable animationTask = new Runnable() {
        @Override
        public void run() {
            binding.overlay.setBackground(new ColorDrawable(Color.argb(150, 255, 255, 255)));
            binding.overlay.postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.overlay.setBackground(null);
                }
            }, 50L);
        }
    };

    @SuppressLint("MissingPermission")
    private void openCamera() {
        cameraManager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
        try {
            characteristics = cameraManager.getCameraCharacteristics(frontCameraId);
            int sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);

            Log.d(TAG, "openCamera: sensorOrientation= " + sensorOrientation);

            binding.viewFinder.setAspectRatio(1080, 800);


            cameraManager.openCamera(frontCameraId, new CameraDevice.StateCallback() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    cameraDevice = camera;
                    Log.d(TAG, "onOpened: ");
                    initCamera();

                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {

                    Log.d(TAG, "onDisconnected: ");
                    cameraDevice = null;
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {

                    Log.e(TAG, "onError: " + error);
                    cameraDevice = null;
                }
            }, cameraHandler);


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化相机配置
     */

    private void initCamera() {

        imageQueue = new ArrayBlockingQueue<Image>(3);
        //用于拍照的
        imageReader = ImageReader.newInstance(1080, 800, ImageFormat.JPEG, 3);
        crateCaption();
    }


    private void crateCaption() {
        List<Surface> outputs = new ArrayList<>();

        outputs.add(previewDataSurface);
        outputs.add(imageReader.getSurface());

        try {
            cameraDevice.createCaptureSession(outputs, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    mSession = session;
                    //构建一个预览的 CaptureRequest
                    CaptureRequest.Builder builder = null;
                    try {
                        builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                        builder.addTarget(previewDataSurface);
                        //开启预览---重复发送captureRequest
                        mSession.setRepeatingRequest(builder.build(), null, null);
                    } catch (CameraAccessException e) {
                        Log.e(TAG, e.getMessage());
                        e.printStackTrace();
                    }

                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                    Log.e(TAG, "onConfigureFailed: ", new Throwable(""));
                }
            }, cameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        if (cameraDevice == null) {
            return;
        }
        CaptureRequest.Builder builder = null;
        ArrayBlockingQueue<Image> imageQueue = new ArrayBlockingQueue<Image>(IMAGE_BUFFER_SIZE);
        imageReader.setOnImageAvailableListener(reader -> {

            Log.d(TAG, "takePhoto: onImageAvailable");
            Image image = reader.acquireNextImage();
            imageQueue.add(image);

        }, imageReaderHandler);
        try {
            builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);

            builder.set(CaptureRequest.JPEG_ORIENTATION, getJpegOrientation(characteristics, 0));

            builder.addTarget(imageReader.getSurface());
            mSession.capture(builder.build(), new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                    super.onCaptureStarted(session, request, timestamp, frameNumber);

                    Log.d(TAG, "onCaptureStarted: ");
                    binding.viewFinder.post(animationTask);
                }

                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);

                    long resultTimestamp = result.get(CaptureResult.SENSOR_TIMESTAMP);

                    Log.d(TAG, "onCaptureCompleted: " + resultTimestamp);
                    int rotation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                    boolean mirrored = characteristics.get(CameraCharacteristics.LENS_FACING) ==
                            CameraCharacteristics.LENS_FACING_FRONT;

                    Log.d(TAG, "onCaptureCompleted: " + rotation + "," + mirrored);
                    try {

                        // Set a timeout in case image captured is dropped from the pipeline
//                        TimeoutException exc =new  TimeoutException("Image dequeuing took too long");
//                        Runnable timeoutRunnable =  new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    throw exc;
//                                } catch (TimeoutException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        };
//                        imageReaderHandler.postDelayed(timeoutRunnable, 5000);

                        while (true) {
                            Log.d(TAG, "onCaptureCompleted: "+imageQueue.size());

                            Image image = imageQueue.take();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                                    image.getTimestamp() != resultTimestamp) {
                                continue;
                            }
                            while (imageQueue.size() > 0) {
                                imageQueue.take().close();
                            }
                            //  保存的图片位置
                            mFile = new File(getContext().getFilesDir(), resultTimestamp + ".jpg");
                            if (!mFile.exists()) {
                                mFile.getParentFile().mkdirs();
                                try {
                                    mFile.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            //  图像保存线程 acquireNextImage 从 ImageReader的队列中获取下一个Image。
                            //  如果没有新图像可用，则返回null 。
                            new Thread(new ImageSaver(image, mFile)).start();
                            return;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull
                        CaptureFailure failure) {
                    super.onCaptureFailed(session, request, failure);
                    Log.e(TAG, "onCaptureFailed: ", new Throwable("拍照异常"));
                }


            }, cameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.e(TAG, "takePhoto: ", e);
        }
    }

    private int getJpegOrientation(CameraCharacteristics cameraCharacteristics, int deviceOrientation) {
        int myDeviceOrientation = deviceOrientation;
        if (myDeviceOrientation == android.view.OrientationEventListener.ORIENTATION_UNKNOWN) {
            return 0;
        }
        int sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);

        // Round device orientation to a multiple of 90
        myDeviceOrientation = (myDeviceOrientation + 45) / 90 * 90;

        // Reverse device orientation for front-facing cameras
        boolean facingFront = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT;
        if (facingFront) {
            myDeviceOrientation = -myDeviceOrientation;
        }

        // Calculate desired JPEG orientation relative to camera orientation to make
        // the image upright relative to the device orientation
        return (sensorOrientation + myDeviceOrientation + 360) % 360;
    }

    /**
     * 图片保存线程
     */
    private class ImageSaver implements Runnable {

        /**
         * JPEG 图片
         */
        private final Image mImage;
        /**
         * 将图像保存到文件
         */
        private final File mFile;

        ImageSaver(Image image, File file) {
            mImage = image;
            mFile = file;
        }

        @Override
        public void run() {
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);

            Bitmap bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
//            Bitmap bitmap = convertBitmap(bitmapImage);

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(mFile);
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                Log.d(TAG, "run: 保存完成");

                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.d(TAG, "run: 相册拍照保存路径" + mFile.getAbsolutePath());
//                        Intent intent = new Intent();
//                        intent.putExtra("uri", Uri.fromFile(mFile));
//                        getActivity().setResult(Activity.RESULT_OK, intent);
//                        getActivity().finish();

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
            }
        }

    }

    /**
     * 左右镜像变换
     *
     * @param srcBitmap
     * @return
     */
    private Bitmap convertBitmap(Bitmap srcBitmap) {
        int width = srcBitmap.getWidth();
        int height = srcBitmap.getHeight();
        Canvas canvas = new Canvas();
        Matrix matrix = new Matrix();

        matrix.postScale(-1, 1);

        Bitmap newBitmap2 = Bitmap.createBitmap(srcBitmap, 0, 0, width, height, matrix, true);

        canvas.drawBitmap(newBitmap2,
                new Rect(0, 0, width, height),
                new Rect(0, 0, width, height), null);

        return newBitmap2;

    }
}
