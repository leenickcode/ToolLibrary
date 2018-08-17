package com.lee.toollibrary.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class FrameTextView extends TextView implements View.OnClickListener{
    private int textColor;
    private int frameColor;
    private int textBGColor;
    private int selectTextColor;
    private int selectFrameColor;
    private int selectTextBGColor;
    private int frameWidth;
    private float round;
    private boolean isClickable  = false;
    private boolean isSelected = false;
    private boolean isNeedInnerFrame = false;
    private OnClick mOnClick;

    public FrameTextView(Context context) {
        super(context);
    }

    public FrameTextView(Context context,AttributeSet attrs) {
        super(context, attrs);
    }

    public FrameTextView(Context context,AttributeSet attrs,int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * 初始化方法
     * @param textColor 字体颜色
     * @param textBGColor 背景色
     * @param frameColor 框颜色 -1为不需要边框
     * @param selectTtextColor 被选中后字体颜色
     * @param selectTextBGColor 被选中后背景色
     * @param selectFrameColor 被选中后框颜色 -1为不需要边框
     * @param round 圆角 0为矩形
     * @param isClickable 是否可点击
     * @param isNeedInnerFrame 是否需要内边框
     * @param frameWidth 内边框宽度
     */
    public void initFrame(int textColor, int textBGColor, int frameColor, int selectTtextColor, int selectTextBGColor, int selectFrameColor, int frameWidth, int round, boolean isClickable, boolean isNeedInnerFrame) {
        this.textColor = textColor;
        this.textBGColor = textBGColor;
        this.frameColor = frameColor;
        this.selectTextColor = selectTtextColor;
        this.selectTextBGColor = selectTextBGColor;
        this.selectFrameColor = selectFrameColor;
        this.round = round;
        this.isClickable = isClickable;
        this.isNeedInnerFrame = isNeedInnerFrame;
        this.frameWidth = frameWidth;
        setTextColor(this.textColor);
        setOnClickListener(this);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0,Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        // 画笔
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        // 创建一个矩形
        Rect rect = canvas.getClipBounds();
        // 创建一个高精矩形
        RectF rectF = new RectF(rect);
        // 设置背景底色
        if(isSelected) {
            paint.setColor(selectTextBGColor);
        }
        else {
            paint.setColor(textBGColor);
        }
        // 画背景
        canvas.drawRoundRect(rectF, round, round, paint);
        if (selectFrameColor != -1 && frameColor != -1) {
            // 设置边框
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(frameWidth);
            // 设置边框色
            if (isSelected) {
                paint.setColor(selectFrameColor);
            } else {
                paint.setColor(frameColor);
            }
            if (isNeedInnerFrame) {
                rectF.left += (frameWidth / 2);
                rectF.top += (frameWidth / 2);
                rectF.bottom -= frameWidth;
                rectF.right -= frameWidth;
            }
            // 画边框
            canvas.drawRoundRect(rectF, round, round, paint);
        }
        super.onDraw(canvas);
    }

    public boolean isTextViewSelected() {
        return isSelected;
    }

    @Override
    public void onClick(View v) {
        if (isClickable){
            isSelected = !isSelected;
            if (isSelected) {
                setTextColor(selectTextColor);
            }else {
                setTextColor(textColor);
            }
            if (mOnClick != null) {
                mOnClick.setOnClick(v);
            }
        }
    }

    public void setOnClick(OnClick listener) {
        mOnClick = listener;
    }

    public interface OnClick {
        void setOnClick(View v);
    }
}
