package com.lee.toollibrary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import com.lee.toollibrary.utils.ConvertUtil
import com.lee.toollibrary.views.NineImageView
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
//        val list: MutableList<NineImageView.Picture> = mutableListOf()
//        val picture = NineImageView.Picture()
//        picture.resouceId = R.drawable.img_3
//        list.add(picture)
//        val picture2 = NineImageView.Picture()
//        picture2.resouceId = R.drawable.img_2
//        list.add(picture2)
//        val picture4 = NineImageView.Picture()
//        picture4.resouceId = R.drawable.img_4
//        list.add(picture4)
//        list.add(picture4)
//        list.add(picture4)
//        nine.setTotalWidth( window.windowManager.defaultDisplay.width)
//        nine.setPictures(list)
        var aa="2019-03-18"
        Log.d("aa",ConvertUtil.getWeek(aa))
    }
}
