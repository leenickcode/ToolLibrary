package com.lee.toollibrary.dialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lee.toollibrary.R;


/**
 * Created by nicklxz on 2018/3/2.
 *  自定义的基本的二次确认对话框/重命名对话框
 *  IOS 风格
 * @author nicklxz
 */

public class IosDefaultDialog {
    private Context mContext;
    private LayoutInflater inflater;
    /**
     * ----公共控件 抛出去好改属性
     */
    /**
     * 提示标题
     */
    public TextView tvTitle;
    /**
     * 取消键
     */
    public Button btCancel;
    /**
     * 确认键
     */
    public Button btConfirm;
    /**
     * 输入内容----用来修改名称/输入金额等
     */
    public EditText editText;
    /**
     * 提示内容
     */
    public TextView tvPrompt;
    /**
     * 图片
     */
    public ImageView ivDialog;
    /**
     * 两个按钮之间的分割线
     */
    private View viewLine;
    /**
     * 取消监听
     */
    private View.OnClickListener cannerListener;
    /**
     * 取人监听
     */
    private View.OnClickListener confirmListener;
    /**
     * dialog布局
     */
    private View contentView;
    /**
     * 提示标题
     */
    private String title;
    /**
     * 提示内容
     */
    private String prompt;
    /**
     * 输入框内容
     */
    private String content;
    public int width, height;
    /**
     * 标准模式  标题+提示+ 取消+确认按钮
     */
    private static final int TYPE_STANDARD = 0;
    /**
     * 单按钮模式 标题+提示+ 取消/确认 按钮
     */
    private static final int TYEPE_SINGLE_BUTTON = 1;
    /**
     * 编辑模式  标题+editText+ 取消+确认按钮
     */
    private static final int TTPE_EDIT = 2;
    /**
     * 无标题模式 prompt+ 取消+确认按钮
     */
    private static final int TPEY_NO_TITILE = 3;
    /**
     * 图片模式-- 无标题   iamge+prompt+cancel+confirm
     */
    private static final int TYPE_IMAGE = 48;
    /**
     * 模式
     */
    private int mode;
    AlertDialog alertDialog;

    public IosDefaultDialog(Context context) {
        this(context, TYPE_STANDARD);
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    /**
     * @param context
     * @param modeId  模式
     */
    public IosDefaultDialog(Context context, int modeId) {
        mContext = context;
        inflater = LayoutInflater.from(context);
        this.mode = modeId;
    }

    /**
     * 初始化控件
     */
    private void init() {
        contentView = inflater.inflate(R.layout.default_dialog, null);
        tvTitle = contentView.findViewById(R.id.tv_title);
        tvTitle.setText("标题");
        editText = contentView.findViewById(R.id.et_nickname);
        btCancel = contentView.findViewById(R.id.bt_cancel);
        btConfirm = contentView.findViewById(R.id.bt_confirm);
        viewLine = contentView.findViewById(R.id.view_line);
        tvPrompt = contentView.findViewById(R.id.tv_prompt);
        ivDialog = contentView.findViewById(R.id.iv_dialog);
        // 屏幕宽度（像素）
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        // 屏幕高度（像素）
        height = mContext.getResources().getDisplayMetrics().heightPixels;
        switch (mode){
            case TYPE_STANDARD:
                editText.setVisibility(View.GONE);
                break;
            case TYEPE_SINGLE_BUTTON:
                editText.setVisibility(View.GONE);
                btCancel.setVisibility(View.GONE);
                viewLine.setVisibility(View.GONE);
                break;
            case TTPE_EDIT:
                tvPrompt.setVisibility(View.GONE);
                break;
            case TPEY_NO_TITILE:
                tvTitle.setVisibility(View.GONE);
                editText.setVisibility(View.GONE);
                break;
            case TYPE_IMAGE:
                tvTitle.setVisibility(View.GONE);
                editText.setVisibility(View.GONE);
                ivDialog.setVisibility(View.VISIBLE);
                break;
                default:
                    editText.setVisibility(View.GONE);

        }
        if (cannerListener != null) {
            btCancel.setOnClickListener(cannerListener);
        }
        if (confirmListener != null) {
            btConfirm.setOnClickListener(confirmListener);
        }
        if (title != null && !"".equals(title)) {
            tvTitle.setText(title);
        }
        if (prompt != null && !"".equals(prompt)) {
            tvPrompt.setText(prompt);
        }
    }

    /**
     * 弹出dialog
     */
    public void show() {
        init();
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        if (contentView != null) {
            builder.setView(contentView);
        }
         alertDialog = builder.show();
        //背景透明
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        alertDialog.show();
        setSize(0.75,0);
    }

    /**
     * dismiss
     */
    public void dismiss(){
        if (alertDialog!=null&&alertDialog.isShowing()){
            alertDialog.dismiss();
        }
    }
    /**
     * 设置取消按钮点击事件
     *
     * @param listener
     */
    public void setCannerListener(View.OnClickListener listener) {
        cannerListener = listener;
    }

    /**
     * 设置确定的点击事件
     *
     * @param listener
     */
    public void setConfirmListener(View.OnClickListener listener) {
        confirmListener = listener;
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    /**
     * 获取输入框的内容
     *
     * @return
     */
    public String getContent() {
        content = editText.getText().toString();
        return content;
    }

    /**
     * 设置 dialog的大小 在show 之后调用
     *
     * @param with   相对于 屏幕宽的比例
     * @param height 相对于 屏幕高的比例
     */
    public void setSize(double with, double height) {
        WindowManager.LayoutParams lp = alertDialog.getWindow().getAttributes();
        if (with == 0) {
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        } else {
            lp.width = (int) (width * with);
        }
        if (height == 0) {
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        } else {
            lp.height = (int) (this.height* height);
        }
        alertDialog.getWindow().setAttributes(lp);
    }

    /**
     * 隐藏一个button和中间的分割线
     */
    public void setHiddenBotton() {
        btCancel.setVisibility(View.GONE);
        viewLine.setVisibility(View.GONE);
    }
}
