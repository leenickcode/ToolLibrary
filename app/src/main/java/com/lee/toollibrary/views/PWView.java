package com.lee.toollibrary.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;

/**
 * Created by nick on 2018/6/6.
 *  密码输入框
 * @author nick
 */
public class PWView extends android.support.v7.widget.AppCompatEditText {
    /**
     * 密码长度 记得在xml里面配置最大长度
     */
    private int textLength = 6;
    Paint borderPaint;
    Paint textPaint;
    /**
     * 每个框之间的空隙
     */
    private int margin = 15;
    /**
     * 每个框的宽度
     */
    private int frameWidth;
    /**
     * 框框颜色
     */
    private int borderColor;


    public PWView(Context context) {
        super(context);
        init();
    }

    public PWView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PWView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        borderPaint = new Paint();
        textPaint = new Paint();
        textPaint.setTextAlign(Paint.Align.CENTER);
        setInputType(InputType.TYPE_CLASS_NUMBER);
        setCursorVisible(false);
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(textLength)});
        //画输入的内容
        textPaint.setTextSize(dip2px(25));
        textPaint.setAntiAlias(true);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //盖一层白色背景 覆盖掉默认的editText背景
        borderPaint.setStyle(Paint.Style.FILL);
        borderPaint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, getWidth(), getHeight(), borderPaint);
        //画框
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(1);
        frameWidth = (getWidth() - (textLength - 1) * margin) / 6;
        for (int i = 0; i < textLength; i++) {
            canvas.drawRoundRect((frameWidth + margin) * i, 0, frameWidth * (i + 1) + margin * i, getHeight(), 10, 10, borderPaint);
        }
        //得到字体的属性 用来群岛
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        //为基线到字体上边框的距离
        float top = fontMetrics.top;
        //为基线到字体下边框的距离
        float bottom = fontMetrics.bottom;
        //基线中间点的y轴计算公式
        int baselineY = (int) (getHeight() / 2 - top / 2 - bottom / 2);
        //画密码
        if (getText() != null && !"".equals(getText().toString())) {
            setSelection(getText().toString().length());
            for (int i = 0; i < getText().toString().length(); i++) {
                //x 方向因为有边框间隔
                canvas.drawText(getText().toString().substring(i, i + 1), frameWidth / 2 + margin * i + frameWidth * i, baselineY, textPaint);
            }
        }


    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
