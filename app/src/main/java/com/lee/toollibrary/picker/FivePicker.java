package com.lee.toollibrary.picker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.lee.toollibrary.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 年月日时分 五级联动
 */
public class FivePicker extends LinearLayout implements YearPicker.OnYearSelectedListener,
        MonthPicker.OnMonthSelectedListener, DayPicker.OnDaySelectedListener, HourPicker.OnHourSelectedListener
        , MinutePicker.OnMinuteSelectedListener {

    private static final String TAG = "FivePicker";
    private YearPicker mYearPicker;
    private MonthPicker mMonthPicker;
    private DayPicker mDayPicker;
    private HourPicker mHourPicker;
    private MinutePicker mMinutePicker;
    private Long mMaxDate;
    private Long mMinDate;
    private OnDateSelectedListener mOnDateSelectedListener;

    /**
     * Instantiates a new Date picker.
     *
     * @param context the context
     */
    public FivePicker(Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new Date picker.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public FivePicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Instantiates a new Date picker.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public FivePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_date_five, this);
        initChild();
        initAttrs(context, attrs);
        mYearPicker.setBackgroundDrawable(getBackground());
        mMonthPicker.setBackgroundDrawable(getBackground());
        mDayPicker.setBackgroundDrawable(getBackground());
    }

    private void initAttrs(Context context, @Nullable AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DatePicker);
        int textSize = a.getDimensionPixelSize(R.styleable.DatePicker_itemTextSize,
                getResources().getDimensionPixelSize(R.dimen.WheelItemTextSize));
        int textColor = a.getColor(R.styleable.DatePicker_itemTextColor,
                Color.BLACK);
        boolean isTextGradual = a.getBoolean(R.styleable.DatePicker_textGradual, true);
        boolean isCyclic = a.getBoolean(R.styleable.DatePicker_wheelCyclic, false);
        int halfVisibleItemCount = a.getInteger(R.styleable.DatePicker_halfVisibleItemCount, 2);
        int selectedItemTextColor = a.getColor(R.styleable.DatePicker_selectedTextColor,
                getResources().getColor(R.color.com_ycuwq_datepicker_selectedTextColor));
        int selectedItemTextSize = a.getDimensionPixelSize(R.styleable.DatePicker_selectedTextSize,
                getResources().getDimensionPixelSize(R.dimen.WheelSelectedItemTextSize));
        int itemWidthSpace = a.getDimensionPixelSize(R.styleable.DatePicker_itemWidthSpace,
                getResources().getDimensionPixelOffset(R.dimen.WheelItemWidthSpace));
        int itemHeightSpace = a.getDimensionPixelSize(R.styleable.DatePicker_itemHeightSpace,
                getResources().getDimensionPixelOffset(R.dimen.WheelItemHeightSpace));
        boolean isZoomInSelectedItem = a.getBoolean(R.styleable.DatePicker_zoomInSelectedItem, false);
        boolean isShowCurtain = a.getBoolean(R.styleable.DatePicker_wheelCurtain, false);
        int curtainColor = a.getColor(R.styleable.DatePicker_wheelCurtainColor, Color.WHITE);
        boolean isShowCurtainBorder = a.getBoolean(R.styleable.DatePicker_wheelCurtainBorder, false);
        int curtainBorderColor = a.getColor(R.styleable.DatePicker_wheelCurtainBorderColor,
                getResources().getColor(R.color.com_ycuwq_datepicker_divider));
        a.recycle();
        Log.e(TAG, "initAttrs: " + textSize);
        setTextSize(textSize);
        setTextColor(textColor);
        setTextGradual(isTextGradual);
        setCyclic(isCyclic);
        setHalfVisibleItemCount(halfVisibleItemCount);
        setSelectedItemTextColor(selectedItemTextColor);
        setSelectedItemTextSize(selectedItemTextSize);
        setItemWidthSpace(itemWidthSpace);
        setItemHeightSpace(itemHeightSpace);
        setZoomInSelectedItem(isZoomInSelectedItem);
        setShowCurtain(isShowCurtain);
        setCurtainColor(curtainColor);
        setShowCurtainBorder(isShowCurtainBorder);
        setCurtainBorderColor(curtainBorderColor);

        mDayPicker.setSuffix("日");
        mMonthPicker.setSuffix("月");
        mYearPicker.setSuffix("年");
        mHourPicker.setSuffix("时");
        mMinutePicker.setSuffix("分");
    }

    private void initChild() {
        mYearPicker = findViewById(R.id.yearPicker_layout_date);
        mYearPicker.setOnYearSelectedListener(this);
        mMonthPicker = findViewById(R.id.monthPicker_layout_date);
        mMonthPicker.setOnMonthSelectedListener(this);
        mDayPicker = findViewById(R.id.dayPicker_layout_date);
        mDayPicker.setOnDaySelectedListener(this);
        mHourPicker = findViewById(R.id.hourPicker_layout_date);
        mHourPicker.setOnHourSelectedListener(this);
        mMinutePicker = findViewById(R.id.minutePicker_layout_date);
        mMinutePicker.setOnMinuteSelectedListener(this);
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        if (mYearPicker != null && mMonthPicker != null && mDayPicker != null
                &&mHourPicker!=null
                &&mMinutePicker!=null) {
            mYearPicker.setBackgroundColor(color);
            mMonthPicker.setBackgroundColor(color);
            mDayPicker.setBackgroundColor(color);
            mHourPicker.setBackgroundColor(color);
            mMinutePicker.setBackgroundColor(color);
        }
    }

    @Override
    public void setBackgroundResource(int resid) {
        super.setBackgroundResource(resid);
        if (mYearPicker != null && mMonthPicker != null && mDayPicker != null&&mHourPicker!=null
                &&mMinutePicker!=null) {
            mYearPicker.setBackgroundResource(resid);
            mMonthPicker.setBackgroundResource(resid);
            mDayPicker.setBackgroundResource(resid);
            mHourPicker.setBackgroundResource(resid);
            mMinutePicker.setBackgroundResource(resid);
        }
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(background);
        if (mYearPicker != null && mMonthPicker != null && mDayPicker != null
                &&mHourPicker!=null
                &&mMinutePicker!=null) {
            mYearPicker.setBackgroundDrawable(background);
            mMonthPicker.setBackgroundDrawable(background);
            mDayPicker.setBackgroundDrawable(background);
            mHourPicker.setBackgroundDrawable(background);
            mMinutePicker.setBackgroundDrawable(background);
        }
    }

    private void onDateSelected() {
        if (mOnDateSelectedListener != null) {
            mOnDateSelectedListener.onDateSelected(getYear(),
                    getMonth(), getDay(),getHour(),getMinute());
        }
    }

    @Override
    public void onYearSelected(int year) {
        int month = getMonth();
        mMonthPicker.setYear(year);
        mDayPicker.setMonth(year, month);
        mHourPicker.setDay(year,getMonth(),getDay());
        mMinutePicker.setHour(year,getMonth(),getDay(),getHour());
        onDateSelected();
    }
    @Override
    public void onMonthSelected(int month) {
        mDayPicker.setMonth(getYear(), month);
        mHourPicker.setDay(getYear(),getMonth(),getDay());
        mMinutePicker.setHour(getYear(),getMonth(),getDay(),getHour());
        onDateSelected();
    }

    @Override
    public void onDaySelected(int day) {
        mHourPicker.setDay(getYear(),getMonth(),getDay());
        mMinutePicker.setHour(getYear(),getMonth(),getDay(),getHour());
        onDateSelected();
    }



    @Override
    public void onHourSelected(int hour) {
        mMinutePicker.setHour(getYear(),getMonth(),getDay(),getHour());
        onDateSelected();
    }

    @Override
    public void onMinuteSelected(int minute) {
        onDateSelected();
    }
    /**
     * Sets date.
     *
     * @param year  the year
     * @param month the month
     * @param day   the day
     */
    public void setDate(int year, int month, int day,int hour,int minute) {
        setDate(year, month, day,hour,minute, true);
    }

    /**
     * Sets date.
     *
     * @param year         the year
     * @param month        the month
     * @param day          the day
     * @param smoothScroll the smooth scroll
     */
    public void setDate(int year, int month, int day, int hour,int minute,boolean smoothScroll) {
        mYearPicker.setSelectedYear(year, smoothScroll);
        mMonthPicker.setSelectedMonth(month, smoothScroll);
        mDayPicker.setSelectedDay(day, smoothScroll);
        mHourPicker.setHour(hour,smoothScroll);
        mMinutePicker.setMinute(minute,smoothScroll);
    }

    public void setMaxDate(long date) {
        setCyclic(false);
        mMaxDate = date;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        mYearPicker.setEndYear(calendar.get(Calendar.YEAR));
        mMonthPicker.setMaxDate(date);
        mDayPicker.setMaxDate(date);
        mMonthPicker.setYear(mYearPicker.getSelectedYear());
        mDayPicker.setMonth(mYearPicker.getSelectedYear(), mMonthPicker.getSelectedMonth());

    }

    /**
     * 设置时间范围 最小
     * @param date
     */
    public void setMinDate(long date) {
        setCyclic(false);
        mMinDate = date;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        mYearPicker.setStartYear(calendar.get(Calendar.YEAR));
        mMonthPicker.setMinDate(date);
        mDayPicker.setMinDate(date);
        Log.e(TAG, "setMinDate: "+calendar.get(Calendar.HOUR_OF_DAY));
        Log.e(TAG, "setMinDate: "+calendar.get(Calendar.MINUTE));
        mHourPicker.setStartHour(calendar.get(Calendar.HOUR_OF_DAY));
        mMinutePicker.setStartMinute(calendar.get(Calendar.MINUTE));
        mMonthPicker.setYear(mYearPicker.getSelectedYear());
        mDayPicker.setMonth(mYearPicker.getSelectedYear(), mMonthPicker.getSelectedMonth());
        mHourPicker.setDay(getYear(),getMonth(),getDay());
        mMinutePicker.setHour(getYear(),getMonth(),getDay(),getHour());
    }

    /**
     * Gets date.
     *
     * @return the date
     */
    public String getDate() {
        DateFormat format = SimpleDateFormat.getDateInstance();
        return getDate(format);
    }

    /**
     * Gets date.
     *
     * @param dateFormat the date format
     * @return the date
     */
    public String getDate(@NonNull DateFormat dateFormat) {
        int year, month, day;
        year = getYear();
        month = getMonth();
        day = getDay();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);

        return dateFormat.format(calendar.getTime());
    }

    /**
     * Gets year.
     *
     * @return the year
     */
    public int getYear() {
        return mYearPicker.getSelectedYear();
    }

    /**
     * Gets month.
     *
     * @return the month
     */
    public int getMonth() {
        return mMonthPicker.getSelectedMonth();
    }

    /**
     * Gets day.
     *
     * @return the day
     */
    public int getDay() {
        return mDayPicker.getSelectedDay();
    }

    /**
     * 获取小时
     * @return
     */
    public int getHour(){
        return mHourPicker.getSelectedHour();
    }

    /**
     * 获取分钟
     * @return
     */
    public int getMinute(){
        return mMinutePicker.getSelectedMinute();
    }
    /**
     * Gets year picker.
     *
     * @return the year picker
     */
    public YearPicker getYearPicker() {
        return mYearPicker;
    }

    /**
     * Gets month picker.
     *
     * @return the month picker
     */
    public MonthPicker getMonthPicker() {
        return mMonthPicker;
    }

    /**
     * Gets day picker.
     *
     * @return the day picker
     */
    public DayPicker getDayPicker() {
        return mDayPicker;
    }

    public HourPicker getHourPicker() {
        return mHourPicker;
    }

    public MinutePicker getMinutePicker() {
        return mMinutePicker;
    }

    /**
     * 一般列表的文本颜色
     *
     * @param textColor 文本颜色
     */
    public void setTextColor(@ColorInt int textColor) {
        mDayPicker.setTextColor(textColor);
        mMonthPicker.setTextColor(textColor);
        mYearPicker.setTextColor(textColor);
        mHourPicker.setTextColor(textColor);
        mMinutePicker.setTextColor(textColor);
    }

    /**
     * 一般列表的文本大小
     *
     * @param textSize 文字大小
     */
    public void setTextSize(int textSize) {
        mDayPicker.setTextSize(textSize);
        mMonthPicker.setTextSize(textSize);
        mYearPicker.setTextSize(textSize);
        mHourPicker.setTextSize(textSize);
        mMinutePicker.setTextSize(textSize);
    }

    /**
     * 设置被选中时候的文本颜色
     *
     * @param selectedItemTextColor 文本颜色
     */
    public void setSelectedItemTextColor(@ColorInt int selectedItemTextColor) {
        mDayPicker.setSelectedItemTextColor(selectedItemTextColor);
        mMonthPicker.setSelectedItemTextColor(selectedItemTextColor);
        mYearPicker.setSelectedItemTextColor(selectedItemTextColor);
        mHourPicker.setSelectedItemTextColor(selectedItemTextColor);
        mMinutePicker.setSelectedItemTextColor(selectedItemTextColor);
    }

    /**
     * 设置被选中时候的文本大小
     *
     * @param selectedItemTextSize 文字大小
     */
    public void setSelectedItemTextSize(int selectedItemTextSize) {
        mDayPicker.setSelectedItemTextSize(selectedItemTextSize);
        mMonthPicker.setSelectedItemTextSize(selectedItemTextSize);
        mYearPicker.setSelectedItemTextSize(selectedItemTextSize);
        mHourPicker.setSelectedItemTextSize(selectedItemTextSize);
        mMinutePicker.setSelectedItemTextSize(selectedItemTextSize);
    }


    /**
     * 设置显示数据量的个数的一半。
     * 为保证总显示个数为奇数,这里将总数拆分，itemCount = mHalfVisibleItemCount * 2 + 1
     *
     * @param halfVisibleItemCount 总数量的一半
     */
    public void setHalfVisibleItemCount(int halfVisibleItemCount) {
        mDayPicker.setHalfVisibleItemCount(halfVisibleItemCount);
        mMonthPicker.setHalfVisibleItemCount(halfVisibleItemCount);
        mYearPicker.setHalfVisibleItemCount(halfVisibleItemCount);
        mHourPicker.setHalfVisibleItemCount(halfVisibleItemCount);
        mMinutePicker.setHalfVisibleItemCount(halfVisibleItemCount);
    }

    /**
     * Sets item width space.
     *
     * @param itemWidthSpace the item width space
     */
    public void setItemWidthSpace(int itemWidthSpace) {
        mDayPicker.setItemWidthSpace(itemWidthSpace);
        mMonthPicker.setItemWidthSpace(itemWidthSpace);
        mYearPicker.setItemWidthSpace(itemWidthSpace);
        mHourPicker.setItemWidthSpace(itemWidthSpace);
        mMinutePicker.setItemWidthSpace(itemWidthSpace);
    }

    /**
     * 设置两个Item之间的间隔
     *
     * @param itemHeightSpace 间隔值
     */
    public void setItemHeightSpace(int itemHeightSpace) {
        mDayPicker.setItemHeightSpace(itemHeightSpace);
        mMonthPicker.setItemHeightSpace(itemHeightSpace);
        mYearPicker.setItemHeightSpace(itemHeightSpace);
        mHourPicker.setItemHeightSpace(itemHeightSpace);
        mMinutePicker.setItemHeightSpace(itemHeightSpace);
    }


    /**
     * Set zoom in center item.
     *
     * @param zoomInSelectedItem the zoom in center item
     */
    public void setZoomInSelectedItem(boolean zoomInSelectedItem) {
        mDayPicker.setZoomInSelectedItem(zoomInSelectedItem);
        mMonthPicker.setZoomInSelectedItem(zoomInSelectedItem);
        mYearPicker.setZoomInSelectedItem(zoomInSelectedItem);
        mHourPicker.setZoomInSelectedItem(zoomInSelectedItem);
        mMinutePicker.setZoomInSelectedItem(zoomInSelectedItem);
    }

    /**
     * 设置是否循环滚动。
     * set wheel cyclic
     *
     * @param cyclic 上下边界是否相邻
     */
    public void setCyclic(boolean cyclic) {
        mDayPicker.setCyclic(cyclic);
        mMonthPicker.setCyclic(cyclic);
        mYearPicker.setCyclic(cyclic);
        mHourPicker.setCyclic(cyclic);
        mMinutePicker.setCyclic(cyclic);
    }

    /**
     * 设置文字渐变，离中心越远越淡。
     * Set the text color gradient
     *
     * @param textGradual 是否渐变
     */
    public void setTextGradual(boolean textGradual) {
        mDayPicker.setTextGradual(textGradual);
        mMonthPicker.setTextGradual(textGradual);
        mYearPicker.setTextGradual(textGradual);
        mHourPicker.setTextGradual(textGradual);
        mMinutePicker.setTextGradual(textGradual);
    }


    /**
     * 设置中心Item是否有幕布遮盖
     * set the center item curtain cover
     *
     * @param showCurtain 是否有幕布
     */
    public void setShowCurtain(boolean showCurtain) {
        mDayPicker.setShowCurtain(showCurtain);
        mMonthPicker.setShowCurtain(showCurtain);
        mYearPicker.setShowCurtain(showCurtain);
        mHourPicker.setShowCurtain(showCurtain);
        mMinutePicker.setShowCurtain(showCurtain);
    }

    /**
     * 设置幕布颜色
     * set curtain color
     *
     * @param curtainColor 幕布颜色
     */
    public void setCurtainColor(@ColorInt int curtainColor) {
        mDayPicker.setCurtainColor(curtainColor);
        mMonthPicker.setCurtainColor(curtainColor);
        mYearPicker.setCurtainColor(curtainColor);
        mHourPicker.setCurtainColor(curtainColor);
        mMinutePicker.setCurtainColor(curtainColor);
    }

    /**
     * 设置幕布是否显示边框
     * set curtain border
     *
     * @param showCurtainBorder 是否有幕布边框
     */
    public void setShowCurtainBorder(boolean showCurtainBorder) {
        mDayPicker.setShowCurtainBorder(showCurtainBorder);
        mMonthPicker.setShowCurtainBorder(showCurtainBorder);
        mYearPicker.setShowCurtainBorder(showCurtainBorder);
        mHourPicker.setShowCurtainBorder(showCurtainBorder);
        mMinutePicker.setShowCurtainBorder(showCurtainBorder);
    }

    /**
     * 幕布边框的颜色
     * curtain border color
     *
     * @param curtainBorderColor 幕布边框颜色
     */
    public void setCurtainBorderColor(@ColorInt int curtainBorderColor) {
        mDayPicker.setCurtainBorderColor(curtainBorderColor);
        mMonthPicker.setCurtainBorderColor(curtainBorderColor);
        mYearPicker.setCurtainBorderColor(curtainBorderColor);
        mHourPicker.setCurtainBorderColor(curtainBorderColor);
        mMinutePicker.setCurtainBorderColor(curtainBorderColor);
    }

    /**
     * 设置选择器的指示器文本
     * set indicator text
     *
     * @param yearText  年指示器文本
     * @param monthText 月指示器文本
     * @param dayText   日指示器文本
     * @param hourText 小时指示器文本
     * @param minuteText   分钟指示器文本
     */
    public void setIndicatorText(String yearText, String monthText, String dayText, String hourText, String minuteText) {
        mYearPicker.setIndicatorText(yearText);
        mMonthPicker.setIndicatorText(monthText);
        mDayPicker.setIndicatorText(dayText);
        mHourPicker.setIndicatorText(hourText);
        mMinutePicker.setIndicatorText(minuteText);
    }

    /**
     * 设置指示器文字的颜色
     * set indicator text color
     *
     * @param textColor 文本颜色
     */
    public void setIndicatorTextColor(@ColorInt int textColor) {
        mYearPicker.setIndicatorTextColor(textColor);
        mMonthPicker.setIndicatorTextColor(textColor);
        mDayPicker.setIndicatorTextColor(textColor);
        mHourPicker.setIndicatorTextColor(textColor);
        mMinutePicker.setIndicatorTextColor(textColor);
    }

    /**
     * 设置指示器文字的大小
     * indicator text size
     *
     * @param textSize 文本大小
     */
    public void setIndicatorTextSize(int textSize) {
        mYearPicker.setTextSize(textSize);
        mMonthPicker.setTextSize(textSize);
        mDayPicker.setTextSize(textSize);
        mHourPicker.setTextSize(textSize);
        mMinutePicker.setTextSize(textSize);
    }



    /**
     * Sets on date selected listener.
     *
     * @param onDateSelectedListener the on date selected listener
     */
    public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
        mOnDateSelectedListener = onDateSelectedListener;
    }


    /**
     * The interface On date selected listener.
     */
    public interface OnDateSelectedListener {
        /**
         * On date selected.
         *
         * @param year  the year
         * @param month the month
         * @param day   the day
         */
        void onDateSelected(int year, int month, int day, int hour, int minute);
    }
}
