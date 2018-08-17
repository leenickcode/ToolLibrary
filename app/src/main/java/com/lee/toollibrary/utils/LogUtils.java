package com.lee.toollibrary.utils;

import android.util.Log;

import com.orhanobut.logger.Logger;


/**
 * log管理类
 * Log是系统自带的普通Log
 * Logger是比较详细的日志打印三方库，需在application中进行配置，打印内容包括 线程，代码位置，输出内容；
 *  implementation 'com.orhanobut:logger:1.14'
 */
public class LogUtils {
    public static void d(String tag,String msg){
        if (BaseUtils.isDebug) {
            Log.d(tag, msg);
        }
    }
    public static void i(String tag,String msg){
        if (BaseUtils.isDebug) {
            Log.i(tag, msg);
        }
    }
    public static void e(String tag,String msg){
        if (BaseUtils.isDebug) {
            Log.e(tag, msg);
        }
    }
    public static void dLoger(String msg){
        if (BaseUtils.isDebug) {
            Logger.d(msg);
        }
    }
    public static void eLoger(String msg){
        if (BaseUtils.isDebug) {
            Logger.e(msg);
        }
    }
    public static void iLoger(String msg){
        if (BaseUtils.isDebug) {
            Logger.i(msg);
        }
    }
    public static void jsonLoger(String json){
        if (BaseUtils.isDebug) {
            Logger.json(json);
        }
    }
}
