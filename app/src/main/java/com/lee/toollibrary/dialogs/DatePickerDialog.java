package com.lee.toollibrary.dialogs;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.lee.toollibrary.R;
import com.lee.toollibrary.picker.DatePicker;

/**
 * Created by Administrator on 2019/4/12.
 *   年月日选择器
 * @author Administrator
 */
public class DatePickerDialog {
    protected DatePicker mDatePicker;
    private int mSelectedYear = -1, mSelectedMonth = -1, mSelectedDay = -1;
    private OnDateChooseListener mOnDateChooseListener;
    private boolean mIsShowAnimation = true;
    protected Button btnDecide, btnCancel;
    private OnDialogBut onDialogBut;
    private View contentView;
    private AlertDialog alertDialog;
    private Context mContext;
    private long miniDate;
    private boolean isShowAllBut = false;

    public DatePickerDialog(Context mContext) {
        this.mContext = mContext;
    }
    public Button getBtnDecide() {
        return btnDecide;
    }
    /**
     * 初始化控件
     */
    private void initView() {
        contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_date, null);
        mDatePicker = contentView.findViewById(R.id.dayPicker_dialog);
        mDatePicker.setSelectedItemTextColor(ContextCompat.getColor(mContext,R.color.green));
        btnDecide = contentView.findViewById(R.id.btn_dialog_date_decide);
        btnCancel = contentView.findViewById(R.id.btn_dialog_date_cancel);
        mDatePicker.setMinDate(miniDate);
        if (onDialogBut != null) {
            onDialogBut.initDatePicker();
        }

        mDatePicker.setOnDateSelectedListener(new DatePicker.OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day) {
                if (mOnDateChooseListener != null) {
                    mOnDateChooseListener.onDateChoose(year, month, day);
                }
                if (onDialogBut != null) {
                    onDialogBut.DateSelecte(year, month, day);
                }
            }
        });


        btnDecide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnDateChooseListener != null) {
                    mOnDateChooseListener.onDateChoose(mDatePicker.getYear(),
                            mDatePicker.getMonth(), mDatePicker.getDay());
                }
                if (onDialogBut != null) {
                    onDialogBut.onDateChoose(mDatePicker.getYear(),
                            mDatePicker.getMonth(), mDatePicker.getDay());
                }
                dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if (mSelectedYear > 0) {
            setSelectedDate();
        }


    }

    public void  show(){
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

    public void  dismiss(){
        if (alertDialog!=null){
            alertDialog.dismiss();
        }
    }

    public void setSelectedDate(int year, int month, int day) {
        mSelectedYear = year;
        mSelectedMonth = month;
        mSelectedDay = day;
        setSelectedDate();
    }

    private void setSelectedDate() {
        if (mDatePicker != null) {
            mDatePicker.setDate(mSelectedYear, mSelectedMonth, mSelectedDay, false);
        }
    }

    public void setOnDateChooseListener(OnDateChooseListener onDateChooseListener) {
        mOnDateChooseListener = onDateChooseListener;
    }

    public void setOnChoseListener(OnDialogBut onDialogButLisntener, boolean isShowAllBut) {
        this.onDialogBut = onDialogButLisntener;
        this.isShowAllBut = isShowAllBut;

    }

    public void setMinDate(long miniDate){
        this.miniDate=miniDate;

    }

    public interface OnDateChooseListener {
        void onDateChoose(int year, int month, int day);

    }

    public interface OnDialogBut extends OnDateChooseListener {

        //全部按钮的点击事件
        void onShowAllDate();

        //滚动

        void DateSelecte(int year, int month, int data);


        //初始化
        void initDatePicker();

    }
}
