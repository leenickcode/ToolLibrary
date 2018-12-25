package com.lee.toollibrary.picker;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

import com.lee.toollibrary.views.WheelPicker;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * MinutePicker
 * Created by ycuwq on 2018/1/22.
 */
public class MinutePicker extends WheelPicker<Integer> {
    private int mStartMinute, mEndMinute;
    private int mSelectedMinute;
    private OnMinuteSelectedListener mOnMinuteSelectedListener;
    private static final String TAG = "MinutePicker";
    public MinutePicker(Context context) {
        this(context, null);
    }

    public MinutePicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MinutePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context,attrs);
        setItemMaximumWidthText("00");
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumIntegerDigits(2);
        setDataFormat(numberFormat);
        updateMinute();
        setOnWheelChangeListener(new OnWheelChangeListener<Integer>() {
            @Override
            public void onWheelSelected(Integer item, int position) {
                mSelectedMinute=item;
                if (mOnMinuteSelectedListener != null) {
                    mOnMinuteSelectedListener.onMinuteSelected(item);
                }
            }
        });
    }
    private void initAttrs(Context context, @Nullable AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        mStartMinute =0;
        mEndMinute =60;
        mSelectedMinute = Calendar.getInstance().get(Calendar.MINUTE);

    }

    /**
     * 设置时间范围--最小
     * @param minute
     */
    public void setStartMinute(int minute){
        Log.d(TAG, "setStartMinute: "+minute);
        mStartMinute =minute;
        updateMinute();
        if (mStartMinute > mSelectedMinute) {
            setMinute(mStartMinute, false);
        } else {
            setMinute(mSelectedMinute, false);
        }
    }
    /**
     * 设置时间范围--最大
     * @param hour
     */
    public void setEndMinute(int hour){
        mEndMinute =hour;
        updateMinute();
        if (mSelectedMinute > mEndMinute) {
            setMinute(mEndMinute, false);
        } else {
            setMinute(mSelectedMinute, false);
        }
    }
    private void updateMinute() {
        List<Integer> list = new ArrayList<>();
        for (int i = mStartMinute ;i < mEndMinute; i++) {
            list.add(i);
        }
        setDataList(list);
    }


    public void setHour(int day,int hour){
        Calendar calendar = Calendar.getInstance();
        int currentDay=calendar.get(Calendar.HOUR_OF_DAY);
        int currentHour=calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute=calendar.get(Calendar.MINUTE);
        if (day==currentDay&&hour==currentHour){
            //当日 当前小时  则小分钟不能小于当前分钟
            mStartMinute=currentMinute;
        }else {
            mStartMinute=0;
        }
        setStartMinute(mStartMinute);
    }
    public void setHour(int year,int month,int day,int hour){
        Log.e(TAG, "setHour: "+year+"--"+month+"--"+day +hour);
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH)+1;
        int currentDay=calendar.get(Calendar.DAY_OF_MONTH);
        int currentHour=calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute=calendar.get(Calendar.MINUTE);
        Log.d(TAG, "setHour: "+currentYear+"--"+currentMonth+"--"+currentDay +currentHour);
        if (year==currentYear&&month==currentMonth&&day==currentDay&&hour==currentHour){
            //当日 当前小时  则小分钟不能小于当前分钟
            mStartMinute=currentMinute;
        }else {
            mStartMinute=0;
        }
        setStartMinute(mStartMinute);
    }
    public int getSelectedMinute(){
        return mSelectedMinute;
    }
    public void setSelectedMinute(int hour) {
        setSelectedMinute(hour, true);
    }

    public void setSelectedMinute(int hour, boolean smootScroll) {
        setCurrentPosition(hour, smootScroll);
    }

    /**
     * 联动时候用到
     * @param hour
     * @param smootScroll
     */
    public void setMinute(int hour, boolean smootScroll){
        setCurrentPosition(hour-mStartMinute, smootScroll);
    }

    public void setOnMinuteSelectedListener(OnMinuteSelectedListener onMinuteSelectedListener) {
        mOnMinuteSelectedListener = onMinuteSelectedListener;
    }

    public interface OnMinuteSelectedListener {
        void onMinuteSelected(int hour);
    }
}
