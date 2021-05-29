package com.lee.toollibrary.downloadLib;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

/**
 * @date 创建时间: 2018/3/7
 * @author nick.li
 * 记得注册
 */


public class DownLoadService extends Service {

    private static DownLoadService mInstance;
    public static String DOWNLOAD = "com.ugood.gmbw";
    private int progress;

    public static DownLoadService getInstance() {
        if (mInstance == null) {
            mInstance = new DownLoadService();
        }
        return mInstance;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public void downLoadFile(final Context context, String url, final String fileName) {
        DownLoadManager manager = new DownLoadManager(context, url, fileName);
        manager.setOnDownLoadListener(new DownLoadManager.ProgressListener() {
            @Override
            public void onStart() {//开始下载
//                subscribeBean.getData().get(pos).setDownLoad(DOWNLOADING);
//                mAdapter.setData(subscribeBean.getData());
                Intent intent = new Intent();
                intent.setAction("com.ugood.gmbw");//用隐式意图来启动广播
                intent.putExtra("fileName", fileName);
                context.sendBroadcast(intent);
            }

            @Override
            public void onProgress(final int pro) {//下载中
                if ((pro - progress) >= 2) {//每2%回调
                    Intent intent = new Intent();
                    intent.setAction("com.ugood.gmbw");//用隐式意图来启动广播
                    intent.putExtra("fileName", fileName);
                    System.out.println("progress == " + pro);
                    context.sendBroadcast(intent);
                    progress = pro;
                }


            }

            @Override
            public void onFinish(String path) {//下载完成
                Intent intent = new Intent();
                intent.setAction(DOWNLOAD);//用隐式意图来启动广播
                intent.putExtra("fileName", fileName);
                context.sendBroadcast(intent);
            }

            @Override
            public void onFailed() {
                Intent intent = new Intent();
                //用隐式意图来启动广播
                intent.setAction(DOWNLOAD);
                intent.putExtra("fileName", fileName);
                context.sendBroadcast(intent);
            }
        });
        manager.download();
    }
}
