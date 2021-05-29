package com.lee.toollibrary.picker;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

import com.lee.toollibrary.views.WheelPicker;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * HourPicker
 * Created by ycuwq on 2018/1/22.
 */
public class HourPicker extends WheelPicker<Integer> {
    private int mStartHour, mEndHour;
    private int mSelectedHour;
    private OnHourSelectedListener mOnHourSelectedListener;
    private static final String TAG = "HourPicker";

    public HourPicker(Context context) {
        this(context, null);
    }

    public HourPicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HourPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        setItemMaximumWidthText("00");
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumIntegerDigits(2);
        setDataFormat(numberFormat);
        updateHour();
        setOnWheelChangeListener(new OnWheelChangeListener<Integer>() {
            @Override
            public void onWheelSelected(Integer item, int position) {
                mSelectedHour=item;
                if (mOnHourSelectedListener != null) {
                    mOnHourSelectedListener.onHourSelected(item);
                }
            }
        });
    }

    private void initAttrs(Context context, @Nullable AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        mStartHour = 0;
        mEndHour = 23;
        mSelectedHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        setSelectedHour(mSelectedHour);
    }

    public int getSelectedHour() {
        return mSelectedHour;
    }

    public void setDay(int year, int month, int day) {
        Log.e(TAG, "setDay: "+year+"--"+month+"--"+day );

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH)+1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        if (year == currentYear && month == currentMonth && day == currentDay) {
            mStartHour = calendar.get(Calendar.HOUR_OF_DAY);
            Log.e(TAG, "setDay: "+mStartHour );
        } else {
            mStartHour = 0;
        }
        setStartHour(mStartHour);
    }

    /**
     * 设置时间范围--最小
     *
     * @param hour
     */
    public void setStartHour(int hour) {
        Log.d(TAG, "setStartHour: " + hour);
        mStartHour = hour;
        updateHour();
        if (mStartHour > mSelectedHour) {
            setHour(mStartHour, false);
        } else {
            setHour(mSelectedHour, false);
        }
    }

    /**
     * 设置时间范围--最大
     *
     * @param hour
     */
    public void setEndHour(int hour) {
        mEndHour = hour;
        updateHour();
        if (mSelectedHour > mEndHour) {
            setHour(mEndHour, false);
        } else {
            setHour(mSelectedHour, false);
        }
    }

    private void updateHour() {
        List<Integer> list = new ArrayList<>();
        for (int i = mStartHour; i <= mEndHour; i++) {
            list.add(i);
        }
        Log.e(TAG, "updateYear: " + list.size());
        setDataList(list);
    }


    public void setSelectedHour(int hour) {
        setSelectedHour(hour, true);
    }

    public void setSelectedHour(int hour, boolean smootScroll) {
        setCurrentPosition(hour, smootScroll);
    }
    public void setHour(int hour, boolean smootScroll){
        setCurrentPosition(hour-mStartHour, smootScroll);
    }

    public void setOnHourSelectedListener(OnHourSelectedListener onHourSelectedListener) {
        mOnHourSelectedListener = onHourSelectedListener;
    }

    public interface OnHourSelectedListener {
        void onHourSelected(int hour);
    }
}
