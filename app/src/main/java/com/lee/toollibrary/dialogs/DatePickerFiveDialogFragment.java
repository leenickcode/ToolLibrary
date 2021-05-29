package com.lee.toollibrary.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.lee.toollibrary.R;
import com.lee.toollibrary.picker.FivePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by nick on 2018/10/26.
 *  5个选择器合在一起  年月日时分
 * @author nick
 * @deprecated
 */
public class DatePickerFiveDialogFragment extends DialogFragment {

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

    private static final String TAG = "DatePickerFiveDialogFra";
    public void showAnimation(boolean show) {
        mIsShowAnimation = show;
    }

    public void setPickerTitle(String pickerTitle) {
        this.pickerTitle = pickerTitle;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_picker_five, container);
        tvDecide =view.findViewById(R.id.tv_ok);
        tvCancel =view.findViewById(R.id.tv_cancel);
        mDatePicker = view.findViewById(R.id.five_picker);

        tvTitle=view.findViewById(R.id.tv_picker_title);
        mDatePicker.setShowCurtain(false);
        mDatePicker.setShowCurtainBorder(false);
        mDatePicker.setSelectedItemTextColor(getResources().getColor(R.color.green));
        Date date=new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        mDatePicker.setMinDate(date.getTime());
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
        initChild();
        return view;
    }

    protected void initChild() {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.DatePickerBottomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置Content前设定

        dialog.setContentView(R.layout.dialog_picker_five);
        dialog.setCanceledOnTouchOutside(true); // 外部点击取消

        Window window = dialog.getWindow();
        if (window != null) {
            if (mIsShowAnimation) {
                window.getAttributes().windowAnimations = R.style.DatePickerDialogAnim;
            }
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.BOTTOM; // 紧贴底部
            lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度持平
            lp.dimAmount = 0.35f;
            window.setAttributes(lp);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        return dialog;
    }

    public void setSelectedDate(int year, int month, int day ) {
        mSelectedYear = year;
        mSelectedMonth = month;
        mSelectedDay = day;
        setSelectedDate();
    }

    /**
     * 设置选中时间
     * @param year
     * @param month
     * @param day
     * @param hours
     * @param minute
     */
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
    }
}
