package com.example.arcmodekt

import android.app.Application


/**
 * @ClassName MyApplication
 * @Description
 * @Author nick
 * @Date 2021/7/6 17:58
 * @Version 1.0
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this


    }


    companion object {
       lateinit var instance: MyApplication
    }




}