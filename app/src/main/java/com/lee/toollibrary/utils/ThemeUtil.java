package com.lee.toollibrary.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.lee.toollibrary.MyApplication;


/**
 * Created by nicklxz on 2018/3/2.
 *
 * @author: nicklxz
 * 主题资源工具类
 * 切记--资源命名不要重复---详细可参考阿里巴巴Android开发手册
 */

public class ThemeUtil {
    private static  Context context;
    private int  colorPrimary;
    private static int colorPrimaryDark;
    private static int colorAccent;

    /**
     * 设置主题
     * @param themeId  主题Id eg: 1
     */
    public static  void setTheme(int themeId){
        context= MyApplication.Companion.getInstance();
        colorAccent= ContextCompat.getColor(context, getColorID("colorAccent_"+themeId));
        colorPrimaryDark= ContextCompat.getColor(context, getColorID("colorPrimaryDark_"+themeId));
    }
    public static int getLayoutID( String layoutName) {
        return context.getResources().getIdentifier(layoutName, "layout", context.getPackageName());
    }

    public static int getStringID( String StringName) {
        return context.getResources().getIdentifier(StringName, "string", context.getPackageName());
    }

    public static int getDrawableID( String DrawableName) {
        return context.getResources().getIdentifier(DrawableName, "drawable", context.getPackageName());
    }

    public static int getStyleID( String StyleName) {
        return context.getResources().getIdentifier(StyleName, "style", context.getPackageName());
    }

    public static int getID( String IdName) {
        return context.getResources().getIdentifier(IdName, "id", context.getPackageName());
    }

    /**
     * 获取_颜色di

     * @param ColorName
     * @return
     */
    public static int getColorID(String ColorName) {
        return context.getResources().getIdentifier(ColorName, "color", context.getPackageName());
    }
}
