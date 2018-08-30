package com.lee.toollibrary.viewAty

import com.bumptech.glide.Glide
import com.lee.toollibrary.BaseActivity
import com.lee.toollibrary.R
import kotlinx.android.synthetic.main.activity_process_image.*

/**
 * Created by nick on 2018/8/20.
 * @author nick
 */
class ProcessImageAty :BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_process_image
    }

    override fun init() {
     Glide.with(this).load("http://119.29.163.91/android/pic/frame_android.png").into(iv_process)
    }

    override fun setListener() {

    }
}