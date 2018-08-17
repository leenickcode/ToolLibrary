package com.lee.toollibrary.utils;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.lee.toollibrary.R;


/**
 * Created by Nick on 2017/3/21.
 * 沉浸式状态栏
 */
public class TranslucentUtils {
    /**
     * 设置状态栏颜色
     * @param activity 需要设置的activity
     * @param color 状态栏颜色值  默认传-1
     */
    public static void setTranslucentColor(Activity activity, int color) {
//        LogUtils.iLoger("setTranslucentColor");
        if(color==-1){
            color= activity.getResources().getColor(R.color.colorAccent);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 生成一个状态栏大小的矩形
            View statusView = createStatusView(activity, color);
            // 添加 statusView 到布局中
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();//注意DecorView是个framelayout
            decorView.addView(statusView);
            // 得到content的子view  framelayout（就是setContentView（）方法中的view的父布局，系统默认会加一个framelayout）
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            if(rootView!=null){
                rootView.setFitsSystemWindows(true);//false 就是通过在 View 上设置和系统窗口一样高度的边框（padding ）来确保你的内容不会出现到系统窗口下面。f反之没有
//                rootView.setClipToPadding(true);//这行不需要吧，默认就是true
            }

        }
    }
    /** * 生成一个和状态栏大小相同的view(矩形条)
     *
    * @param activity 需要设置的activity
     * @param color 状态栏颜色值
     * @return 状态栏矩形条
     */
    private static View createStatusView(Activity activity, int color) {
        // 获得状态栏高度
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
//        LogUtils.iLoger(statusBarHeight+"");
        // 绘制一个和状态栏一样高的矩形
        View statusView = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                statusBarHeight);
        statusView.setLayoutParams(params);
        statusView.setBackgroundColor(color);
        return statusView;
    }
}
