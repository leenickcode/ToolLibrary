package com.lee.toollibrary.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.lee.toollibrary.R;
import com.lee.toollibrary.views.WheelPicker;

import java.util.ArrayList;

/**
 * Created by nick on 2018/8/30.
 *
 * @author nick
 */
public class PikerDialogFragment extends DialogFragment {
    TextView tvCancel, tvOk;
    /**
     * 滚轮选择器
     */
    WheelPicker<String> wheelPicker;
    /**
     * 自定义view
     */
    View contentView;
    /**
     * 布局宽高
     */
    private int width,height;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity(), R.style.Dialog_Fullscreen);
        // 关闭标题栏，setContentView() 之前调用
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_single_pinck, null);
        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(true);
        initView();
        initData();
        return dialog;
    }

    private void initView() {
        wheelPicker = contentView.findViewById(R.id.wp_picker);
        tvCancel = contentView.findViewById(R.id.tv_cancel);
        tvOk = contentView.findViewById(R.id.tv_ok);
        // 屏幕宽度（像素）
        width = getResources().getDisplayMetrics().widthPixels;
        height = getResources().getDisplayMetrics().heightPixels;
    }

    private void initData() {
        ArrayList<String> arrayList = new ArrayList();
        for (int i = 0; i < 10; i++) {
            arrayList.add("" + i);
        }
        wheelPicker.setDataList(arrayList);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.75), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    /**
     * 设置 dialog的大小 在show 之后调用
     *
     * @param with   相对于 屏幕宽的比例
     * @param height 相对于 屏幕高的比例
     */
    public void setSize(double with, double height) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        if (with == 0) {
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        } else {
            lp.width = (int) (width * with);
        }
        if (height == 0) {
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        } else {
            lp.height = (int) (height * height);
        }
        getActivity().getWindow().setAttributes(lp);
        getActivity().getWindow().setGravity(Gravity.TOP);
    }

//    @Override
//    public void show(FragmentManager manager, String tag) {
//        super.show(manager, tag);
//        setSize(width,0);
//    }

}
