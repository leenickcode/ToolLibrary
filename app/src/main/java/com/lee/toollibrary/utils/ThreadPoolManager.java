package com.lee.toollibrary.utils;

import androidx.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by nick on 2018/7/4.
 *  线程池管理类
 * @author nick
 */
public class ThreadPoolManager {

    /**
     * 单例设计模式（饿汉式）
     *  单例首先私有化构造方法，然后饿汉式一开始就开始创建，并提供get方法
     */
    private static ThreadPoolManager mInstance = new ThreadPoolManager();
    public static ThreadPoolManager getInstance() {
        return mInstance;
    }

    /**
     * 核心线程池的数量，同时能够执行的线程数量
     */
    private int corePoolSize;
    /**
     * 最大线程池数量，表示当缓冲队列满的时候能继续容纳的等待任务的数量
     */
    private int maximumPoolSize;
    /**
     * 存活时间
     */
    private long keepAliveTime = 1;
    private ThreadPoolExecutor executor;
    private static final String TAG = "ThreadPoolManager";
    /**
     * 原子操作的Integer类，其实就是线程安全的Integer类
     */
    private final AtomicInteger integer = new AtomicInteger();
    private ThreadPoolManager() {
        /**
         * 给corePoolSize赋值：当前设备可用处理器核心数*2 + 1,能够让cpu的效率得到最大程度执行（有研究论证的）
         */
        corePoolSize = Runtime.getRuntime().availableProcessors()*2+1;
        //虽然maximumPoolSize用不到，但是需要赋值，否则报错
        maximumPoolSize = corePoolSize;
        Log.d(TAG, "ThreadPoolManager: "+corePoolSize+"----"+maximumPoolSize);
        executor = new ThreadPoolExecutor(
                //当某个核心任务执行完毕，会依次从缓冲队列中取出等待任务
                corePoolSize,
                //5,先corePoolSize,然后new LinkedBlockingQueue<Runnable>(),然后maximumPoolSize,但是它的数量是包含了corePoolSize的
                maximumPoolSize,
                //表示的是maximumPoolSize当中等待任务的存活时间
                keepAliveTime,
                //时间的单位
                TimeUnit.MINUTES,
                //缓冲队列，用于存放等待任务，Linked的先进先出
                new LinkedBlockingQueue<Runnable>(),
                //创建线程的工厂
                new ThreadFactory() {
                    @Override
                    public Thread newThread(@NonNull Runnable r) {
                        String threadName = "MyThreadPoolManager thread-";
                        return new Thread(r, threadName + integer.getAndIncrement());
                    }
                },
                //用来对超出maximumPoolSize的任务的处理策略
        new ThreadPoolExecutor.AbortPolicy()
        );
    }
    /**
     * 执行任务
     */
    public void execute(Runnable runnable){
        if(runnable==null) {
            return;
        }
        executor.execute(runnable);
    }
    /**
     * 从线程池中移除任务
     */
    public void remove(Runnable runnable){
        if(runnable==null) {
            return;
        }
        executor.remove(runnable);
    }
}
