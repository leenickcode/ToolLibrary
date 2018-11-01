package com.lee.toollibrary

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        init()
        setListener()
    }
    /**
     * @return xml 布局id
     */
    protected abstract fun getLayoutId(): Int


    /**
     * 初始化
     */
    protected abstract fun init()

    /**
     * 设计时间监听
     */
    protected abstract fun setListener()
    protected abstract fun business()
}
