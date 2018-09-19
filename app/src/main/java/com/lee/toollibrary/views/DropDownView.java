package com.lee.toollibrary.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.lee.toollibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick on 2018/8/31.
 * 下拉列表
 *
 * @author nick
 */
public class DropDownView extends LinearLayout {
    private LayoutInflater inflater;
    private PopupWindow popupWindow;
    private List<String> data;
    private PopAdapter popAdapter;
    private static final String TAG = "DropDwonView";
    private TextView tvSelect;
    /**
     * 默认选中
     */
    private int selected;
    /**
     * 由于设置了点击外部导致pop会先dismiss后show,所以加这个判断
     */
    private boolean isShowing;

    public DropDownView(Context context) {
        super(context);
        inflater = LayoutInflater.from(context);
        initView();
    }

    public DropDownView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflater = LayoutInflater.from(context);
        initView();
    }

    public DropDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflater = LayoutInflater.from(context);
        initView();
    }

    public DropDownView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflater = LayoutInflater.from(context);
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        Log.d("", "initView: ");
        View view = inflater.inflate(R.layout.custom_drop_dwon, this, true);
        tvSelect = view.findViewById(R.id.tv_select);
        data = new ArrayList<>();
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowing) {
                    if (popupWindow != null) {
                        if (popupWindow.isShowing()) {
                            closePopWindow();
                        }
                    }
                    isShowing = false;
                } else {
                    isShowing = true;
                    if (popupWindow == null) {
                        showPopWindow();
                    } else {
                        if (popupWindow.isShowing()) {
                        } else {
                            showPopWindow();
                        }
                    }
                }


            }
        });
    }

    /**
     * 设置数据源
     *
     * @param data 数据
     */
    public void setData(List<String> data) {
        this.data = data;
        if (popAdapter != null) {
            popAdapter.setData(data);
            popAdapter.notifyDataSetChanged();
        }
        tvSelect.setText(data.get(selected));
    }



    /**
     * 设置选中
     *
     * @param selected int
     */
    public void setSelected(int selected) {
        this.selected = selected;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 弹出选项列表
     */
    public void showPopWindow() {
        View view = inflater.inflate(R.layout.custom_pop_drop_dwon, null);
        RecyclerView recyclerView = view.findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        popAdapter = new PopAdapter(data, getContext());
        recyclerView.setAdapter(popAdapter);
        Log.d(TAG, "showPopWindow: " + getWidth());
        if (popupWindow == null) {
            popupWindow = new PopupWindow(view, getWidth(),
                    ConstraintLayout.LayoutParams.WRAP_CONTENT);
        }

        // 点击popuwindow外让其消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.BLUE));
        popupWindow.showAsDropDown(this);
    }

    /**
     * 关闭选项列表
     */
    public void closePopWindow() {
        Log.d(TAG, "closePopWindow: ");
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    class PopAdapter extends RecyclerView.Adapter<MyViewHolder> {
        LayoutInflater adapterInflater;
        private List<String> data;

        public PopAdapter(List<String> data, Context context) {
            this.data = data;
            adapterInflater = LayoutInflater.from(context);
        }

        public void setData(List<String> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = adapterInflater.inflate(R.layout.recycle_item_dropdwon, parent,false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
            holder.textView.setText(data.get(position));
            holder.textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected = position;
                    tvSelect.setText(data.get(position));
                    closePopWindow();
                    if (itemClick != null) {
                        itemClick.onItemClick(position, data.get(position));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_item);
        }
    }

    public interface ItemClick {
        void onItemClick(int position, String item);
    }

    private ItemClick itemClick;

    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }
}
