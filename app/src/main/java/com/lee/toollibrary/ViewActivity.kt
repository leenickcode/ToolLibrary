package com.lee.toollibrary

import android.view.View
import com.lee.toollibrary.views.NineImageView
import kotlinx.android.synthetic.main.activity_view.*

/**
 * Created by nick on 2018/8/15.
 * @author nick
 * 展示各种自定义View的Activity
 */
class ViewActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_view
    }

    override fun init() {

        val type = intent.getIntExtra("type", -1)
        when (type) {
            0 -> pw_view.visibility = View.VISIBLE
            1 -> {
            }
            2 -> {
            }
            3 -> {

            }
            4 -> {
                circle_image_view.visibility = View.VISIBLE
            }
            5 -> {
                val list: MutableList<NineImageView.Picture> = mutableListOf()
                val picture = NineImageView.Picture()
                picture.resouceId = R.drawable.img_3
                list.add(picture)

                nine_image_view.setTotalWidth(1080)
                nine_image_view.setPictures(list)
            }

            else -> {

            }
        }
    }

    override fun setListener() {

    }
}