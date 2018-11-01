package com.lee.toollibrary.viewAty

import android.view.View
import com.lee.toollibrary.BaseActivity
import com.lee.toollibrary.R
import com.lee.toollibrary.views.NineImageView
import kotlinx.android.synthetic.main.activity_view.*

/**
 * Created by nick on 2018/8/15.
 * @author nick
 * 展示各种自定义View的Activity
 */
class ViewActivity : BaseActivity() {
    val ddvList: MutableList<String> = mutableListOf()
    override fun getLayoutId(): Int {
        return R.layout.activity_view
    }

    override fun init() {

        val type = intent.getStringExtra("type")
        when (type) {
            "密码输入框" -> pw_view.visibility = View.VISIBLE

            "wheelView"->{

            }
            "圆形imageView"->{
                circle_image_view.visibility = View.VISIBLE
            }
            "九宫格imageView"->{
                val list: MutableList<NineImageView.Picture> = mutableListOf()
                val picture = NineImageView.Picture()
                picture.resouceId = R.drawable.img_3
                list.add(picture)

                nine_image_view.setTotalWidth(1080)
                nine_image_view.setPictures(list)
            }
            "进度条imageView"->{

            }

            "下拉列表"->{
                ddvList.add("1")
                ddvList.add("2")
                ddvList.add("3")
                ddv.visibility = View.VISIBLE
                ddv.setData(ddvList)
            }
            else -> {

            }
        }
    }

    override fun setListener() {

    }
}