package com.lee.toollibrary

import android.app.Application

/**
 * Created by nicklxz on 2017/12/29.
 */

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    //伴生对象， 在kt中没有静态方法和静态变量，它使用包级函数或伴生对象来实现，包级函数都是静态，
    //而伴生对象也是在类加载的时候就初始化，类对象都共享该类的伴生对象
    companion object {
        var instance: MyApplication? = null
    }
}
