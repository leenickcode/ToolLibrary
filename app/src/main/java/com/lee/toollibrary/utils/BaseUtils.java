package com.lee.toollibrary.utils;



import android.app.Application;

import com.lee.toollibrary.MyApplication;


/**
 * 由于工具类中为了简化使用代码，直接把application写入，未避免工具类多了以后，替换包名不方便的问题，把几个关键变量在这里进行替换和修改包名
 */
public class BaseUtils {
    public static Application application = MyApplication.Companion.getInstance();
    public static boolean     isDebug     =true;
//    protected static boolean isDebug=false;
}


