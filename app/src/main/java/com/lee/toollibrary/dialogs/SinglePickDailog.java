package com.lee.toollibrary.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.lee.toollibrary.R;

/**
 * Created by nick on 2018/8/30.
 * 单项滚轮选择器
 * @author nick
 */
public class SinglePickDailog extends Dialog {
    TextView tvCancel,tvOk;
    public SinglePickDailog(@NonNull Context context) {
        super(context, R.style.Dialog_Fullscreen);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_single_pinck);
        initView();
        initListener();
        initData();
    }

    /**
     * 初始化view
     */
    private  void initView(){
        tvCancel=findViewById(R.id.tv_cancel);
        tvOk=findViewById(R.id.tv_ok);

    }

    /**
     * 初始化监听
     */
    private void initListener(){
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData(){

    }
}
