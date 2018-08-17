package com.lee.toollibrary.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nicklxz on 2017/11/20.
 *滚轮选择器
 *
 */
//        1首先WheelView继承ScrollView，这样就很好的解决了上下滚动的问题。
//        2WheelView里面add了一个 LinearLayout作为它的唯一子View。LinearLayout竖向排列，当调用
//        3setItems(List<String> list) 的时候就是给Linearlayout添加一个个的子view，竖向排列，并设置Linearlayout和WheelView的高度为3个Item的高度。
//        4当所有的Item都添加完后，会触发protected void onSizeChanged(int w, int h, int oldw, int oldh)，在这里设置WheelView的背景，也就是夹着中间Item的上下两根蓝色线。
//        5在onTouchEvent(MotionEvent ev)方法中，当手指抬起的时候处理移动的距离不是一个Item高度的整数倍的情况，让Linearlayout缓慢滚动的位置上。
//        6最后onScrollChanged(int l, int t, int oldl, int oldt)方法处理每个Item的字体的颜色。

public class WheelView extends ScrollView {
    private static final String TAG = "hhp";
    private Context context;
    private LinearLayout views;
    List<String> items;
    public static final int OFF_SET_DEFAULT = 1;
    private int offset = OFF_SET_DEFAULT; // 偏移量（需要在最前面和最后面补全）
    private int displayItemCount; // 每页显示的数量
    private  int initialY; //纵向滑动距离
    private  Runnable scrollerTask;
    private  int anInt = 50;//50毫秒
    private   int selectedIndex = 1;//当前选中的item 下标
    private   int itemHeight = 0;//main_recycle_item 高度
    /**
     * textView的padding
     */
    private int itemPaddingTop;
    private int itemPaddingBottom;
    private int itemPaddingLeft;
    private int itemPaddingRight;
    public WheelView(Context context) {
        super(context);
        this.context=context;
        init(context);

    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init(context);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context=context;
        init(context);
    }

    private List<String> getItems() {
        return items;
    }

    public void setItems(List<String> list) {
//        Log.d(TAG, "setItems() called with: list = [" + list + "]");
        if (null == items) {
            items = new ArrayList<String>();
        }
        items.clear();
        if (list != null) {
            items.addAll(list);
        }
        // 前面和后面补全
        for (int i = 0; i < offset; i++) {
            items.add(0, "");
            items.add("");
        }
        initData();
    }

    /**
     * 设置数据源
     * @param list  数据源
     * @param forCount  循环次数
     */
    public void setItems(List<String> list,int forCount){
        if (null == items) {
            items = new ArrayList<String>();
        }
        items.clear();
        if (list != null) {
            for (int i = 0; i <forCount ; i++) {
                items.addAll(list);
            }
        }
        // 前面和后面补全
        for (int i = 0; i < offset; i++) {
            items.add(0, "");
            items.add("");
        }
        selectedIndex=items.size()/2;
        setSeletion(selectedIndex);
        initData();
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }


    private void init(Context context) {
        this.context = context;
        this.setVerticalScrollBarEnabled(false);

        views = new LinearLayout(getContext());
        views.setPadding(0,0,0,0);
        views.setOrientation(LinearLayout.VERTICAL);
        this.addView(views);
        scrollerTask = new Runnable() {
            public void run() {
                int newY = getScrollY();
                if (initialY - newY == 0) {
                    // stopped
                    final int remainder = initialY % itemHeight;
                    final int divided = initialY / itemHeight;
                    if (remainder == 0) {
                        //刚刚好滑动到item的正中心
                        selectedIndex = divided + offset;
                        onSeletedCallBack();
                    } else {//有偏差
                        if (remainder > itemHeight / 2) {
                            //大于一半滑动到下一个
                            WheelView.this.post(new Runnable() {
                                @Override
                                public void run() {
                                    WheelView.this.smoothScrollTo(0, initialY - remainder + itemHeight);
                                    selectedIndex = divided + offset + 1;
                                    onSeletedCallBack();
                                }
                            });
                        } else {
                            //小于一半滑动到上一个
                            WheelView.this.post(new Runnable() {
                                @Override
                                public void run() {
                                    WheelView.this.smoothScrollTo(0, initialY - remainder);
                                    selectedIndex = divided + offset;
                                    onSeletedCallBack();
                                }
                            });
                        }

                    }

                } else {
                    initialY = getScrollY();
                    WheelView.this.postDelayed(scrollerTask, anInt);
                }
            }
        };

    }



    private void initData() {
        displayItemCount = offset * 2 + 1;
        views.removeAllViews();
        for (String item : items) {
            views.addView(createView(item));
        }
        refreshItemView(0);
    }

    /**
     * 创建TextView
     * @param itemName item内容
     * @return  TextView
     */
    private TextView createView(String itemName) {
        TextView tv = new TextView(getContext());
        tv.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setSingleLine(true);
        tv.setPadding(itemPaddingLeft,itemPaddingTop,itemPaddingRight,itemPaddingBottom);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        tv.setText(itemName);
        tv.setGravity(Gravity.CENTER);
//        int padding = dip2px(15);
//        tv.setPadding(padding, padding, padding, padding);
        if (0 == itemHeight) {
            itemHeight = getViewMeasuredHeight(tv);
            views.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight * displayItemCount));
           ViewGroup.LayoutParams lp =null ;
            if(this.getLayoutParams() instanceof LinearLayout.LayoutParams){
                lp= (LinearLayout.LayoutParams) this.getLayoutParams();
            }else if (this.getLayoutParams() instanceof ConstraintLayout.LayoutParams){
                lp= (ConstraintLayout.LayoutParams) this.getLayoutParams();
            }
            lp.height=itemHeight * displayItemCount;
            this.setLayoutParams(lp);
        }
        return tv;
    }



    /**
     * 设置每一个Item字体颜色
     * @param y   滚动的距离
     */
    private void refreshItemView(int y) {
        int position = y / itemHeight + offset;
        //相对于一个item移动的距离
        int remainder = y % itemHeight;
        int divided = y / itemHeight;

        if (remainder == 0) {
//       移动距离不到1个item
            position = divided + offset;
        } else {
            if (remainder > itemHeight / 2) {
                position = divided + offset + 1;
            }
        }

        int childSize = views.getChildCount();
        for (int i = 0; i < childSize; i++) {
            TextView itemView = (TextView) views.getChildAt(i);
            if (null == itemView) {
                return;
            }
            if (position == i) {
                //选中的字体颜色
                itemView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
                //黑色
                itemView.setTextColor(Color.parseColor("#000000"));
            } else {
                itemView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                itemView.setTextColor(Color.parseColor("#bbbbbb"));
            }
        }
    }

    /**
     * 获取选中区域的边界
     */
    int[] selectedAreaBorder;

    private int[] obtainSelectedAreaBorder() {
        if (null == selectedAreaBorder) {
            selectedAreaBorder = new int[2];
            selectedAreaBorder[0] = itemHeight*offset ;
            selectedAreaBorder[1] = itemHeight*(offset+1);
//            selectedAreaBorder[0] = getViewMeasuredHeight(this)/lock_fang-itemHeight/lock_fang ;
//            selectedAreaBorder[open_lock_one] = getViewMeasuredHeight(this)/lock_fang+itemHeight/lock_fang;
        }
        return selectedAreaBorder;
    }

    private int scrollDirection = -1;
    private static final int SCROLL_DIRECTION_UP = 0;
    private static final int SCROLL_DIRECTION_DOWN = 1;

    Paint paint;
    int viewWidth;

    /**
     * 绘制背景
     * @param background
     */
    @Override
    public void setBackgroundDrawable(Drawable background) {
        if (viewWidth == 0) {
//            viewWidth = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
            viewWidth= getWidth();
        }

        if (null == paint) {
            paint = new Paint();
            paint.setColor(Color.parseColor("#DEDEDE"));
            paint.setStrokeWidth(dip2px(1f));
        }

        background = new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                canvas.drawLine(0, obtainSelectedAreaBorder()[0], viewWidth , obtainSelectedAreaBorder()[0], paint);
                canvas.drawLine(0, obtainSelectedAreaBorder()[1], viewWidth, obtainSelectedAreaBorder()[1], paint);
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(ColorFilter cf) {

            }

            @Override
            public int getOpacity() {
                return PixelFormat.UNKNOWN;
            }
        };
        super.setBackgroundDrawable(background);

    }
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        refreshItemView(t);
        Log.d(TAG, "onScrollChanged: ");
        if (t > oldt) {
            // Log.d(TAG, "向下滚动");
            scrollDirection = SCROLL_DIRECTION_DOWN;
        } else {
            // Log.d(TAG, "向上滚动");
            scrollDirection = SCROLL_DIRECTION_UP;

        }

    }

    /**
     * 绘制线
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged() called with: w = [" + w + "], h = [" + h + "], oldw = [" + oldw + "], oldh = [" + oldh + "]");
        viewWidth = w;
        setBackgroundDrawable(null);
    }
    @Override
    public void fling(int velocityY) {
        //加快停下的速度
        super.fling(velocityY / 2);
    }

    /**
     * 判断华东的距离  以及单击事件
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        Log.d(TAG, "onTouchEvent() called with: ev = [" + ev + "]");
        if (ev.getAction() == MotionEvent.ACTION_UP) {
//            LogUtils.dLoger("onTouchEvent");
            initialY = getScrollY();
            this.postDelayed(scrollerTask, anInt);
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 选中回调
     */
    private void onSeletedCallBack() {
        if (null != onWheelViewListener) {
            onWheelViewListener.onSelected(selectedIndex, items.get(selectedIndex));
        }
    }

    public void setSeletion(int position) {
        final int p = position;
        selectedIndex = p + offset;
        this.post(new Runnable() {
            @Override
            public void run() {
                WheelView.this.smoothScrollTo(0, p * itemHeight);
            }
        });

    }

    public String getSeletedItem() {
        return items.get(selectedIndex);
    }

    public int getSeletedIndex() {
        return selectedIndex - offset;
    }



    private OnWheelViewListener onWheelViewListener;

    public OnWheelViewListener getOnWheelViewListener() {
        return onWheelViewListener;
    }

    /**
     * 设置选中监听
     * @param onWheelViewListener
     */
    public void setOnWheelViewListener(OnWheelViewListener onWheelViewListener) {
        this.onWheelViewListener = onWheelViewListener;
    }

    /**
     * 设置padding来控制每个item的高度
     * @param left  左边距离
     * @param top  上边距离
     * @param right 右边距离
     * @param bottom 下边距离
     */
    public void setTextViewPadding(int left,int top,int right,int bottom){
           itemPaddingLeft=dip2px(left);
           itemPaddingTop=dip2px(top);
           itemPaddingRight=dip2px(right);
           itemPaddingBottom=dip2px(bottom);
    }
    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 获取view的高度
     * @param view
     * @return
     */
    private int getViewMeasuredHeight(View view) {
        int width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
        return view.getMeasuredHeight();
    }

    public static interface OnWheelViewListener {
        public void onSelected(int selectedIndex, String item);
    }


}

