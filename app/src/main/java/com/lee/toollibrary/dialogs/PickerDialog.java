package com.lee.toollibrary.dialogs;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


import com.lee.toollibrary.R;
import com.lee.toollibrary.views.WheelPicker;

import java.util.ArrayList;

/**
 * Created by Administrator on 2019/4/12.
 * 单个次数选择器
 * @author Administrator
 */
public class PickerDialog {
    private Context mContext;
    private WheelPicker<Integer> mPicker;
    private ArrayList<Integer> mList;
    private Button btnDecide, btnCancel;
    private View contentView;
    private AlertDialog alertDialog;

    public PickerDialog(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 初始化控件
     */
    private void initView() {

        contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_single_picker, null);
        btnDecide = contentView.findViewById(R.id.btn_dialog_decide);
        btnCancel = contentView.findViewById(R.id.btn_dialog_cancel);
        mPicker = contentView.findViewById(R.id.wp_single);
        mList = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            mList.add(i);
        }
        mPicker.setDataList(mList);
        mPicker.setSelectedItemTextColor(ContextCompat.getColor(mContext,R.color.green));
        btnDecide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectListener != null) {
                    mSelectListener.onSelected(String.valueOf(mPicker.getItem()), mPicker.getCurrentPosition());
                }
                alertDialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    /**
     * 显示dialog
     */
    public void show() {
        initView();
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        if (contentView != null) {
            builder.setView(contentView);
        }
        alertDialog = builder.show();
        //背景透明
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);

        Window window = alertDialog.getWindow();
        if (window != null) {
            window.getAttributes().windowAnimations = R.style.DatePickerDialogAnim;
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.BOTTOM; // 紧贴底部
            lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度持平
            lp.dimAmount = 0.35f;
            window.setAttributes(lp);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        alertDialog.show();
    }

    /**
     * 选中监听
     */
    private SelectListener mSelectListener;

    public void setSelectListener(SelectListener listener) {
        mSelectListener = listener;
    }

    public interface SelectListener {
        void onSelected(String item, int position);
    }
}
