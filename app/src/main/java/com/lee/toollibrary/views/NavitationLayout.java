package com.lee.toollibrary.views;


import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 联动viewpager顶部导航栏
 */
public class NavitationLayout extends RelativeLayout {

    /**
     * 使用方法----
     *  String[] title = {"参与的项目", "被邀请的项目"};
        tabLayout.setNavWidth(mActivity,20);
         tabLayout.setMargin(80);//如果xml设置了margin 左右各40，这边就设置80
         tabLayout.setViewPager(mActivity, title, vpViewpage, R.color.C272D2F, R.color.C272D2F, 16,
         16, 0, 0, true);
        tabLayout.setBgLine(mActivity, 1, R.color.white);
         tabLayout.setNavLine(mActivity, 5, R.color.C272D2F, 0);
     */
    private TextView[] textViews; // 标题栏数组，用于存储要显示的标题
    private LinearLayout titleLayout; //标题栏父控件
    private ViewPager viewPager;

    private View bgLine; //导航背景色
    private RelativeLayout navLineGroup; //导航条

    private int navWidth = 0; //导航条宽度
    private int navGroupWidth = 0;//导航条外部容器宽度，
    private int txtUnselectedColor = 0;
    private int txtSelectedColor = 0;
    private int txtUnselectedSize = 16;
    private int txtSelectedSize = 16;
//    private int widOffset          = 0;

    private OnTitleClickListener onTitleClickListener;
    private OnNaPageChangeListener onNaPageChangeListener;
    private static final String TAG = "NavitationLayout";

    public NavitationLayout(Context context) {
        this(context, null);
    }

    public NavitationLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavitationLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        titleLayout = new LinearLayout(context);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        titleLayout.setLayoutParams(layoutParams);
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);
        addView(titleLayout);
    }

    public void setOnTitleClickListener(OnTitleClickListener onTitleClickListener) {
        this.onTitleClickListener = onTitleClickListener;
    }

    public void setOnNaPageChangeListener(OnNaPageChangeListener onNaPageChangeListener) {
        this.onNaPageChangeListener = onNaPageChangeListener;
    }

    private void setTitles(Context context, String[] titles, final boolean smoothScroll) {
        this.textViews = new TextView[titles.length];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
        params.weight = 1;
        params.gravity = Gravity.CENTER;
        // 循环，根据标题栏动态生成TextView来显示标题，每个标题栏的宽度比例为1:1,其中的内容居中。
        for (int i = 0; i < titles.length; i++) {
            final int index = i;
            TextView textView = new TextView(context);
            textView.setText(titles[i]);
            textView.setGravity(Gravity.CENTER);
            textViews[i] = textView;
            textViews[i].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: " + index + "====" + viewPager.getCurrentItem() + "---" + smoothScroll);
                    viewPager.setCurrentItem(index, smoothScroll);
                    if (onTitleClickListener != null) {
                        onTitleClickListener.onTitleClick(v);
                    }
                }
            });
            titleLayout.addView(textView, params);
        }
    }


    private void setTitles(Context context, String[] titles, final boolean smoothScroll, int splilinecolor, float splilinewidth, float topoffset, float bottomoffset) {
        this.textViews = new TextView[titles.length];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
        params.weight = 1;
        params.gravity = Gravity.CENTER;

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dip2px(context, splilinewidth), LayoutParams.MATCH_PARENT);
        lp.setMargins(0, dip2px(context, topoffset), 0, dip2px(context, bottomoffset));
        // 循环，根据标题栏动态生成TextView来显示标题，每个标题栏的宽度比例为1:1,其中的内容居中。
        for (int i = 0; i < titles.length; i++) {
            final int index = i;
            TextView textView = new TextView(context);
            textView.setText(titles[i]);
            textView.setGravity(Gravity.CENTER);
            textViews[i] = textView;
            //点击标题栏滑动viewpage
            textViews[i].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    viewPager.setCurrentItem(index, smoothScroll);
                    if (onTitleClickListener != null) {
                        onTitleClickListener.onTitleClick(v);
                    }
                }
            });
            titleLayout.addView(textView, params);
            if (i < titles.length - 1) {
                View view = new View(context);
                view.setBackgroundColor(splilinecolor);
                titleLayout.addView(view, lp);
            }
        }

    }

    /**
     * 设置导航背景色
     *
     * @param context
     * @param height
     * @param color
     */
    public void setBgLine(Context context, int height, int color) {
        height = dip2px(context, height);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, height);
        bgLine = new View(context);
        bgLine.setLayoutParams(layoutParams);
        bgLine.setBackgroundColor(context.getResources().getColor(color));

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, height);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        addView(bgLine, lp);
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    /**
     * 设置导航条颜色
     *
     * @param context
     * @param height 高度
     * @param color
     * @param currentPosition 当前选中
     */
    private int margin = 0;

    /**
     * 设置导航条宽度
     *
     * @param context
     * @param navWidth
     */
    public void setNavWidth(Context context, int navWidth) {
        this.navWidth = dip2px(context, navWidth);
    }

    /**
     * 设置 导航条
     *
     * @param context
     * @param height
     * @param color
     * @param currentPosition
     */
    public void setNavLine(Activity context, int height, int color, int currentPosition) {
        if (textViews != null) {
            navGroupWidth = (getScreenWidth(context) - dip2px(context, margin)) / textViews.length;
        }

        if (navWidth == 0) {
            //默认跟文字的宽度一样
            if (textViews != null) {
                navWidth = (getScreenWidth(context) - dip2px(context, margin)) / textViews.length;
            }
        }
        height = dip2px(context, height);
        //导航条外部套的容器
        LayoutParams layoutParams = new LayoutParams(navGroupWidth, height);
        navLineGroup = new RelativeLayout(context);
        navLineGroup.setLayoutParams(layoutParams);
        //导航条
        View navLine = new View(context);
        LayoutParams layoutParams1 = new LayoutParams(navWidth, height);
        layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
        navLine.setLayoutParams(layoutParams1);
        navLine.setBackgroundColor(context.getResources().getColor(color));
        navLineGroup.addView(navLine);
        LayoutParams lp = new LayoutParams(navGroupWidth, height);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        Log.d(TAG, "setNavLine: " + navGroupWidth + "---" + navWidth);
        addView(navLineGroup, lp);
        moveBar(navLineGroup, navGroupWidth, 0, currentPosition);
    }

    /**
     * @param context           上下文
     * @param titles            标题栏
     * @param viewPager
     * @param unselectedcolor   未选中字体颜色
     * @param setectedcolor     选中字体颜色
     * @param txtUnselectedSize 未选中字体大小
     * @param txtSelectedSize   选中字体大小
     * @param currentPosition   当前viewpager的位置
     * @param widOffset         导航条的边距  目前不用
     * @param smoothScroll      滑动类型
     */
    public void setViewPager(final Context context, String[] titles, ViewPager viewPager,
                             final int unselectedcolor, final int setectedcolor, int txtUnselectedSize,
                             final int txtSelectedSize, final int currentPosition, int widOffset, boolean smoothScroll) {

        this.viewPager = viewPager;
        this.txtUnselectedColor = unselectedcolor;
        this.txtSelectedColor = setectedcolor;
        this.txtUnselectedSize = txtUnselectedSize;
        this.txtSelectedSize = txtSelectedSize;
//        this.widOffset = dip2px(context, widOffset);
        viewPager.setCurrentItem(currentPosition);
        setTitles(context, titles, smoothScroll);
        setUnselectedTxtColor(context, unselectedcolor, txtUnselectedSize);
        setSelectedTxtColor(context, setectedcolor, txtSelectedSize, currentPosition);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d(TAG, "onPageScrolled: " + positionOffset + "---" + positionOffsetPixels);
                moveBar(navLineGroup, navGroupWidth, positionOffset, position);
                if (onNaPageChangeListener != null) {
                    onNaPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                setSelectedTxtColor(context, setectedcolor, txtSelectedSize, position);
                if (onNaPageChangeListener != null) {
                    onNaPageChangeListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (onNaPageChangeListener != null) {
                    onNaPageChangeListener.onPageScrollStateChanged(state);
                }
            }
        });
    }


    public void setViewPager(final Context context, String[] titles, ViewPager viewPager, final int unselectedcolor,
                             final int setectedcolor, int txtUnselectedSize, final int txtSelectedSize,
                             final int currentPosition, int widOffset, boolean smoothScroll, int splilinecolor,
                             float splilinewidth, float topoffset, float bottomoffset) {
        this.viewPager = viewPager;
        this.txtUnselectedColor = unselectedcolor;
        this.txtSelectedColor = setectedcolor;
        this.txtUnselectedSize = txtUnselectedSize;
        this.txtSelectedSize = txtSelectedSize;
//        this.widOffset = dip2px(context, widOffset);

        viewPager.setCurrentItem(currentPosition);
        setTitles(context, titles, smoothScroll, splilinecolor, splilinewidth, topoffset, bottomoffset);
        setUnselectedTxtColor(context, unselectedcolor, txtUnselectedSize);
        setSelectedTxtColor(context, setectedcolor, txtSelectedSize, currentPosition);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                moveBar(navLineGroup, navGroupWidth, positionOffset, position);
                if (onNaPageChangeListener != null) {
                    onNaPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                setSelectedTxtColor(context, setectedcolor, txtSelectedSize, position);
                if (onNaPageChangeListener != null) {
                    onNaPageChangeListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (onNaPageChangeListener != null) {
                    onNaPageChangeListener.onPageScrollStateChanged(state);
                }
            }
        });
    }

    private void moveBar(View bar, int width, float percent, int position) {
        Log.d(TAG, "moveBar: " + width);
        LayoutParams lp = (LayoutParams) bar.getLayoutParams();
        int marginleft = (position) * width + (int) (width * percent);
//        lp.width = width - widOffset * 2;
        lp.width = width;
//        lp.setMargins(marginleft + widOffset, 0, widOffset, 0);
        lp.setMargins(marginleft, 0, 0, 0);
        bar.requestLayout();
    }

    //
//    public void moveBar2(i){
//
//    }
    private void setUnselectedTxtColor(Context context, int unselectedcolor, int unselectedsize) {
        if (textViews != null) {
            int length = textViews.length;
            for (int i = 0; i < length; i++) {
                textViews[i].setTextColor(context.getResources().getColor(unselectedcolor));
                textViews[i].setTextSize(unselectedsize);
            }
        }
    }

    private void setSelectedTxtColor(Context context, int selectedcolor, int selectedsize, int position) {
        if (textViews != null) {
            int length = textViews.length;
            for (int i = 0; i < length; i++) {
                if (i == position) {
                    textViews[i].setTextColor(context.getResources().getColor(selectedcolor));
                    textViews[i].setTextSize(selectedsize);
                } else {
                    textViews[i].setTextColor(context.getResources().getColor(txtUnselectedColor));
                    textViews[i].setTextSize(txtUnselectedSize);
                }
            }
        }
    }


    /**
     * 获取屏幕宽度
     *
     * @param
     * @return
     */
    private static int getScreenWidth(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 点击标题栏事件
     */
    public interface OnTitleClickListener {
        void onTitleClick(View v);
    }

    /**
     * viewpager滑动事件
     */
    public interface OnNaPageChangeListener {
        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageSelected(int position);

        void onPageScrollStateChanged(int state);
    }

    /**
     * 给外面做联动
     *
     * @param position
     * @param positionOffset
     */
    public void move(int position, float positionOffset) {
        moveBar(navLineGroup, navGroupWidth, positionOffset, position);
    }


}
