package com.lee.toollibrary.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Shader;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick on 2018/11/5.
 *   用贝塞尔曲线画的一波浪view
 * @author nick
 */
public class BezierView extends View {
    /**
     * 画点的笔
     */
    private Paint mPaint;
    /**
     * 画曲线的笔
     */
    private Paint xPaint;
    /**
     * 选中效果  圆环的笔
     */
    private  Paint selectPaint;
    /**
     * 选中效果的实心的笔
     */
    private Paint selectPointPaint;
    /**
     * 曲线路径
     */
    private Path mPath;


    private int maxHeight = 350;    // 最高点的坐标值
    private int minHeight = 150;    // 最低点的坐标值
    private int levHeight = 250;    // 水平线坐标值
    private int goBottomLength = 500;  //水平线到底部的距离
    private int len = 400;//一个正弦函数的长度
    private int startX = 0;      // 绘制的起点
    float lastDownX = 0;
    float lastDownY = 0;
    /**
     * 对应开门记录的点的集合
     */
    private List<Point> pointList;
    /**
     * 画曲线的数据点
     */
    private List<Point> startPoints;
    /**
     * 画曲线的控制点
     */
    private List<Point> assPoints;


    private List<Point> endPonits;

    private static final String TAG = "jiayou";

    private VelocityTracker mTracker;
    /**
     * 最大可以Fling的距离
     */
    private int mMaxFlingX, mMinFlingX;
    /**
     * 滑动时的最小/最大速度
     */
    private int mMinimumVelocity = 50, mMaximumVelocity = 1200;
    private boolean scrollRight;
    /**
     * 当前选中的点的下标
     */
    int selectPointIndex;

    public BezierView(Context context) {
        super(context);
        init();
    }

    public BezierView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BezierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BezierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mPaint = new Paint();
        xPaint = new Paint();
        selectPaint=new Paint();
        mPaint.setColor(Color.RED);
        xPaint.setAntiAlias(true);
        // 防抖动
        xPaint.setDither(true);
        xPaint.setColor(Color.GREEN);
        //设置渐变
        Shader shader = new LinearGradient(0, maxHeight, 0, levHeight + goBottomLength, Color.parseColor("#00d085"),
                Color.parseColor("#ffffff"), Shader.TileMode.CLAMP);
        xPaint.setShader(shader);
        mPath = new Path();
        endPonits = new ArrayList<>();
        startPoints = new ArrayList<>();
        assPoints = new ArrayList<>();
        pointList = new ArrayList<>();
        len = 800;
        mScroller = new Scroller(getContext());
        selectPaint.setStyle(Paint.Style.STROKE);
        selectPaint.setStrokeWidth(5);
        selectPointPaint=new Paint();
        selectPointPaint.setColor(Color.GREEN);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        pointList.clear();
        startPoints.clear();
        assPoints.clear();
        endPonits.clear();
        Point startPoint1 = new Point(startX, levHeight);
        Point assPoint1 = new Point(startX + len / 2, maxHeight);
        Point endPoint1 = new Point(startX + len, levHeight);
        startPoints.add(startPoint1);
        assPoints.add(assPoint1);
        endPonits.add(endPoint1);

        Point startPoint2 = new Point(endPoint1.x, endPoint1.y);
        Point endPoint2 = new Point(startPoint2.x + len, levHeight);
        Point assPoint2 = new Point((startPoint2.x + endPoint2.x) / 2, minHeight);
        startPoints.add(startPoint2);
        assPoints.add(assPoint2);
        endPonits.add(endPoint2);

        Point startPoint3 = new Point(endPoint2.x, endPoint2.y);
        Point endPoint3 = new Point(startPoint3.x + len, levHeight);
        Point assPoint3 = new Point((startPoint3.x + endPoint3.x) / 2, maxHeight);
        startPoints.add(startPoint3);
        assPoints.add(assPoint3);
        endPonits.add(endPoint3);


        //绘制曲线
        mPath.reset();
        mPath.moveTo(startPoint1.x, startPoint1.y);
        mPath.quadTo(assPoint1.x, assPoint1.y, endPoint1.x, endPoint1.y);
        mPath.quadTo(assPoint2.x, assPoint2.y, endPoint2.x, endPoint2.y);
        mPath.quadTo(assPoint3.x, assPoint3.y, endPoint3.x, endPoint3.y);
        mPath.lineTo(endPoint3.x, minHeight + goBottomLength);
        mPath.lineTo(startX, minHeight + goBottomLength);
        canvas.drawPath(mPath, xPaint);


        //通过 二阶贝塞尔函数公式得到 曲线上的点
        float t = (float) 1;
        int x = getBezierPoint(startPoint1.x, assPoint1.x, endPoint1.x, t);
        int y = getBezierPoint(startPoint1.y, assPoint1.y, endPoint1.y, t);
        Point point = new Point(x, y);
        pointList.add(point);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x, y, 15, mPaint);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3);
        canvas.drawCircle(x, y, 15, mPaint);
         t = (float) 0.5;

        Point point2 = new Point(getBezierPoint(startPoint1.x, assPoint1.x, endPoint1.x, t),  getBezierPoint(startPoint1.y, assPoint1.y, endPoint1.y, t));
        pointList.add(point2);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(point2.x, point2.y, 15, mPaint);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3);
        canvas.drawCircle(point2.x, point2.y, 15, mPaint);

        //绘制选中
        selectPaint.setColor(Color.GREEN);
        selectPaint.setShadowLayer(10, 0, 0, Color.RED);
        canvas.drawCircle(pointList.get(selectPointIndex).x,pointList.get(selectPointIndex).y,25,selectPaint);

        canvas.drawCircle(pointList.get(selectPointIndex).x,pointList.get(selectPointIndex).y,15,selectPointPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mTracker == null) {
            mTracker = VelocityTracker.obtain();
        }
        mTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTracker.clear();
                lastDownX = event.getX();
                lastDownY = event.getY();
                clickRange(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                startX = (int) (event.getX() - lastDownX) + startX;
                if (event.getX() - lastDownX > 0) {
                    //  //右滑是  绘制点大于0说明滑到了边界
                    if (startX >= 0) {
                        startX = 0;
                    }
                    scrollRight = true;
                } else {
                    //     左滑时
                    scrollRight = false;
                    if (startX <= -len) {
                        startX = -len;
                    }
                }
                lastDownX = event.getX();
                Log.d(TAG, "onTouchEvent: ");
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                //更具当期的绘制起点得到最大滑动距离
                if (scrollRight) {
                    mMaxFlingX = 0 - startX;
                } else {
                    mMaxFlingX = len - Math.abs(startX);
                }
                mTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocity = (int) mTracker.getXVelocity();
                snapToScreen(velocity);
                if (mScroller.getFinalX() > mMaxFlingX) {
                    mScroller.setFinalX(mMaxFlingX);
                } else if (mScroller.getFinalX() < mMinFlingX) {
                    mScroller.setFinalX(mMinFlingX);
                }
                if (mTracker != null) {
                    mTracker.clear();
                    mTracker.recycle();
                    mTracker = null;
                }
                break;
        }
        return true;

    }

    Scroller mScroller;

    private void snapToScreen(int velocityX) {
        synchronized (BezierView.this) {
            mScroller.fling(Math.round(getScrollX()), 0, -Math.round(velocityX), 0, 0, Integer.MAX_VALUE, 0, 0);
        }
        postInvalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    /**
     * 通过公司得到 贝塞尔曲线上的点
     *
     * @param a 数据点
     * @param b 控制点
     * @param c 数据点
     * @param t
     */
    private int getBezierPoint(int a, int b, int c, float t) {
        return (int) (Math.pow(1 - t, 2) * a + 2 * t * (1 - t) * b + Math.pow(t, 2) * c);
    }

    /**
     * 判断点击的范围
     *
     * @param x 点击的坐标x
     * @param y 点击的坐标y
     */
    private void clickRange(float x, float y) {
        for (int i = 0; i < pointList.size(); i++) {
            if (Math.abs(pointList.get(i).x - x) <= 35) {
                if (Math.abs(pointList.get(i).y - y) <= 35) {
                    //说明点击了该点
                    selectPointIndex = i;
                    invalidate();
                    return;
                }
            }
        }
    }
}
