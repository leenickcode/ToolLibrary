package com.lee.toollibrary.views;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.lee.toollibrary.R;

import java.util.List;

/**
 * Created by nicklxz on 2018/3/17.
 * 九宫格图片--根据不同图片数量改变图片的宽高
 * @author nicklxz
 */

public class NineImageView extends ViewGroup {
    private Context mContext;
    /**
     * 图片集合
     */
    private List<Picture> pictures;
    /**
     * 每张子图片的高度
     */
    private int childHeight;
    /**
     * 每张子图片的宽度
     */
    private int childWidth;
    /**
     * 容器总宽
     */
    private int totalWidth;
    /**
     * 当前行数
     */
    private int rows;
    /**
     * 当前列数
     */
    private  int column;
    /**
     * 图片之间的间隔 默认5px
     */
    private int gap=8;
    public NineImageView(Context context) {
        super(context);
        mContext = context;

    }

    public NineImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public NineImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NineImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    /**
     * 设置容器的宽  一般直接给屏幕宽 有margin/padding 记得处理
     * @param width
     */
    public void setTotalWidth(int width){
        totalWidth=width;
        totalWidth=totalWidth-getPaddingLeft()-getPaddingRight();
    }
    /**
     * 设置图片集
     * @param pictures
     */
    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
        /**
         * 根据图片的数量设置图片的宽高
         */
        if (pictures.size() > 0) {
            if (pictures.size() == 1) {
                rows=1;
                column =1;
                childWidth = totalWidth;
                childHeight = totalWidth;
            } else if (pictures.size() <= 4) {
                column =2;
                if (pictures.size()<=2){
                    rows=1;
                }  else {
                    rows=2;
                }
                childWidth = totalWidth / 2;
                childHeight =totalWidth / 2;
            } else if (pictures.size() > 4) {
                column =3;
                if (pictures.size()<6){
                    rows=2;
                }
                childWidth = totalWidth / 3;
                childHeight = totalWidth / 3;
            }
            for (int i = 0; i < pictures.size(); i++) {
                createImageView(i);
            }
            layoutChildrenView();
        }
    }

    /**
     * 创建单个Imageview
     */
    private void createImageView(int i) {
        ImageView childView = new ImageView(mContext);
        childView.setImageResource(pictures.get(i).getResouceId());
        childView.setScaleType(ImageView.ScaleType.CENTER_CROP
        );
        addView(childView,generateDefaultLayoutParams());
    }

    /**
     * 重新排布子view
     */
    private void layoutChildrenView(){
        int childrenCount = pictures.size();
        //根据子view数量确定高度
        LayoutParams params = getLayoutParams();
        params.height = childHeight * rows + gap * (rows - 1);
        setLayoutParams(params);
        for (int i = 0; i < childrenCount; i++) {
            ImageView childrenView = (ImageView) getChildAt(i);
//            childrenView.setImageUrl(((Image) listData.get(i)).getUrl());
            int[] position = getImagePos(i);
            int left = (childWidth + gap) * position[1]+getPaddingLeft();
            int top = (childHeight + gap )* position[0]+getPaddingTop();
            int right = left + childWidth;
            int bottom = top + childHeight;
            childrenView.layout(left, top, right, bottom);
        }

    }

    /**
     * 得到当前图片所在的行 列
     * @param childIndex  第几个图片
     * @return
     */
    private int[] getImagePos(int childIndex) {
        int[] position = new int[2];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < column; j++) {
                if ((i * column + j) == childIndex) {
                    //行
                    position[0] = i;
                    //列
                    position[1] = j;
                    break;
                }
            }
        }
        return position;
    }
    /**
     * 图片对象
     */
    public static class Picture {
        private int width;
        private int height;
        /**
         * 图片路径
         */
        private String url;
        private int resouceId;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getResouceId() {
            return resouceId;
        }

        public void setResouceId(int resouceId) {
            this.resouceId = resouceId;
        }

        // TODO: 2018/3/17 还只是个demo,没有把网路加载图片放进来
        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }
}
