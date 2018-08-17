package com.lee.toollibrary.downloadLib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @date 创建时间: 2018/3/7
 * @author nick.li
 * 下载文件工具类
 */


public class DownLoadManager {
    /* 开始下载 */
    private static final int START = 3;
    /* 下载中 */
    private static final int DOWNLOAD = 1;
    /* 下载结束 */
    private static final int DOWNLOAD_FINISH = 2;
    /** 下载失败 */
    private static final int DOWNLOAD_FAILED = 420;
    /* 保存解析的XML信息 */
    // HashMap<String, String> mHashMap;
    /* 下载保存路径 */
    private String mSavePath;
    /* 记录进度条数量 */
    private int progress;
    /* 是否取消更新 */
    private boolean cancelUpdate = false;

    private Context mContext;
    private String appUrl;
    private String appName;
    private ProgressListener listener;

    public interface ProgressListener {
        void onStart();
        void onProgress(int pro);
        void onFinish(String path);
        void onFailed();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 开始下载
                case START:
                    if (listener != null) {
                        listener.onStart();
                    }
                    break;
                // 正在下载
                case DOWNLOAD:
                    // 设置进度条位置
                    int lastProgress = progress;
                    if (listener != null) {
                        listener.onProgress(progress);
                    }
//                    dialogProgress.setProgress(progress);
                    break;
                case DOWNLOAD_FINISH:
                    // 安装文件
//                    installApk();
                    if (listener != null) {
                        listener.onFinish(mSavePath);
                    }
//                    mContext.showToast("下载完成");
                    break;
                case DOWNLOAD_FAILED:
                    if (listener != null) {
                        listener.onFailed();
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };

    public DownLoadManager(Context context, String url, String appName) {
        this.mContext = context;
        this.appUrl = url;
        this.appName = appName;
    }

    /**
     *
     * @param context
     * @param url  文件路径
     * @param appName
     * @param isDwonDialog  是否显示下载dialog
     */
    public DownLoadManager(Context context, String url, String appName, boolean isDwonDialog) {
        this.mContext = context;
        this.appUrl = url;
        this.appName = appName;
    }
    public void setOnDownLoadListener(ProgressListener listener) {
        this.listener = listener;
    }

    /**
     * 下载文件
     */
    public void download() {
        // 启动新线程下载软件
        new downloadApkThread().start();
    }


    /**
     * 下载文件线程
     *
     * @author coolszy
     * @date 2012-4-26
     * @blog http://blog.92coding.com
     */
    private class downloadApkThread extends Thread {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(START);
            try {
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    // 获得存储卡的路径
                    String sdpath = Environment.getExternalStorageDirectory()
                            + "/";
                    mSavePath = sdpath + "gm";
                    URL url = new URL(appUrl);
                    // 创建连接
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.connect();
                    // 获取文件大小
                    int length = conn.getContentLength();
                    // 创建输入流
                    InputStream is = conn.getInputStream();

                    File file = new File(mSavePath);
                    // 判断文件目录是否存在
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    File apkFile = new File(mSavePath, appName);
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    // 缓存
                    byte buf[] = new byte[1024];
                    // 写入到文件中
                    do {
                        int numread = is.read(buf);
                        count += numread;
                        // 计算进度条位置
                        progress = (int) (((float) count / length) * 100);
                        // 更新进度
                        mHandler.sendEmptyMessage(DOWNLOAD);
                        if (numread <= 0) {
                            // 下载完成
                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                            break;
                        }
                        // 写入文件
                        fos.write(buf, 0, numread);
                    } while (!cancelUpdate);// 点击取消就停止下载.
                    fos.close();
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(DOWNLOAD_FAILED);
            }finally {

            }
            // 取消下载对话框显示
//			dialogProgress.dismiss();
        }
    }

    ;

    /**
     * 安装APK文件
     */
    private void installApk() {
        File apkfile = new File(mSavePath, appName);
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
    }
}
