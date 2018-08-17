package com.lee.toollibrary.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Nick on 2017/1/18.
 */

public class ToastUtil {
    private static Toast toast;
    public static void show(Context context, CharSequence msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void show(Context context, @StringRes int stringId) {
        Toast.makeText(context, stringId, Toast.LENGTH_LONG).show();
    }

    public static void show(View view, CharSequence msg) {
        show(view.getContext(), msg);
    }

    public static void show(View view, @StringRes int stringId) {
        show(view.getContext(), stringId);
    }
    /**
     * 显示短时间的Toast
     * @param msg 输出信息
     */
    public static void showToast(String msg) {
        if (toast==null) {
            toast = Toast.makeText(BaseUtils.application, msg, Toast.LENGTH_SHORT);
        }else{
            toast.setText(msg);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }
}
