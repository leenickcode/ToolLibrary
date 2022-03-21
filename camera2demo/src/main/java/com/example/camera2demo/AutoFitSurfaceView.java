package com.example.camera2demo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

import kotlin.math.MathKt;

/**
 * Created by nicklee on 2022/3/21.
 *
 * @author nicklee
 */

public class AutoFitSurfaceView extends SurfaceView {
    private float aspectRatio;
    private static final String TAG = "AutoFitSurfaceView";
    public AutoFitSurfaceView(Context context) {
        super(context);
    }

    public AutoFitSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoFitSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AutoFitSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public final void setAspectRatio(int width, int height) throws  IllegalArgumentException{
        boolean var3 = width > 0 && height > 0;

        if (!var3) {
            String var7 = "Size cannot be negative";
            throw new IllegalArgumentException(var7.toString());
        } else {

            this.aspectRatio = (float)width / (float)height;

            this.getHolder().setFixedSize(getWidth(), MathKt.roundToInt((float)width / aspectRatio));
            this.requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        Log.d(TAG, "Measured dimensions set: " + width + " x " + height);
        Log.d(TAG, "Measured dimensions set: " + aspectRatio);
        if (aspectRatio == 0.0F) {
            setMeasuredDimension(width, height);
        } else {
            int newWidth;
            int newHeight;
//            float actualRatio = width > height ? this.aspectRatio : 1.0F / this.aspectRatio;
//            newHeight =  MathKt.roundToInt((float)width / aspectRatio);
            if ((float)width < (float)height * aspectRatio) {
                newHeight = height;
                newWidth = MathKt.roundToInt((float)height * aspectRatio);
            } else {
                newWidth = width;
                newHeight = MathKt.roundToInt((float)width / aspectRatio);
            }

            Log.d(TAG, "Measured dimensions set: " + newWidth + " x " + newHeight);
            setMeasuredDimension(newWidth, newHeight);
        }

    }


}
