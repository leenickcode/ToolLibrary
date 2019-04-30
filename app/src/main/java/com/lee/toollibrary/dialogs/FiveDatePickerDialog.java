package com.lee.toollibrary.dialogs;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


import com.lee.toollibrary.R;
import com.lee.toollibrary.picker.FivePicker;

import java.util.List;

/**
 * Created by Administrator on 2019/4/12.
 *  年月日时分 5例选择器
 * @author Administrator
 */
public class FiveDatePickerDialog {

    private View contentView;
    private AlertDialog alertDialog;
    private Context mContext;

    protected FivePicker mDatePicker;
    private int mSelectedYear = -1, mSelectedMonth = -1, mSelectedDay = -1,mSelectedHours=-1,mSelectedMinute;

    private boolean mIsShowAnimation = true;
    protected TextView tvDecide, tvCancel,tvTitle;
    /**
     * 小时集合
     */
    private List<Integer> hourList;
    /**
     * 分钟集合
     */
    private List<Integer> minuteList;
    /**
     * 上面的标题
     */
    private String pickerTitle;
    private long minDate;
    private static final String TAG = "DatePickerFiveDialogFra";
    public void showAnimation(boolean show) {
        mIsShowAnimation = show;
    }

    public FiveDatePickerDialog(Context mContext) {
        this.mContext = mContext;
    }

    public void setPickerTitle(String pickerTitle) {
        this.pickerTitle = pickerTitle;
    }
    /**
     * 初始化控件
     */
    private void initView() {
        contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_picker_five, null);
        tvDecide =contentView.findViewById(R.id.tv_ok);
        tvCancel =contentView.findViewById(R.id.tv_cancel);
        mDatePicker = contentView.findViewById(R.id.five_picker);

        tvTitle=contentView.findViewById(R.id.tv_picker_title);
        mDatePicker.setShowCurtain(false);
        mDatePicker.setShowCurtainBorder(false);
        mDatePicker.setSelectedItemTextColor(ContextCompat.getColor(mContext,R.color.green));

        mDatePicker.setMinDate(minDate);
        tvDecide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSelectListener(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDay()
                            ,mDatePicker.getHour(),mDatePicker.getMinute());
                }
                dismiss();

            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if (mSelectedYear > 0) {
            setSelectedDate();
        }
        if (pickerTitle!=null){
            tvTitle.setText(pickerTitle);
        }
    }
    public void setMinDate(long date) {
     minDate=date;
    }
    public void  dismiss(){
        if (alertDialog!=null){
            alertDialog.dismiss();
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


    public void setSelectedDate(int year, int month, int day ) {
        mSelectedYear = year;
        mSelectedMonth = month;
        mSelectedDay = day;
        setSelectedDate();
    }
    public void setSelectedDate(int year, int month, int day,int hours,int minute) {
        mSelectedYear = year;
        mSelectedMonth = month;
        mSelectedDay = day;
        mSelectedHours=hours;
        mSelectedMinute=minute;
        setSelectedDate();
    }

    private void setSelectedDate() {
        if (mDatePicker != null) {
            mDatePicker.setDate(mSelectedYear, mSelectedMonth, mSelectedDay, mSelectedHours,mSelectedMinute,false);
        }
    }

    public void setListener(onSelectListener listener) {
        this.listener = listener;
    }

    private  onSelectListener listener;
    public interface  onSelectListener{
        void  onSelectListener(int year, int month, int day, int hour, int minute);
//        void  onSelectListenerToString(String year, String month,String day ,String hour,String minute);
    }
}
