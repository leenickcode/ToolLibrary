package com.example.jetpackdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.jetpackdemo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        val userLiveData = MutableLiveData<User>()
        val user = User("张", "san")
        userLiveData.value = user
        binding.user = userLiveData
        //与livedata结合要设置对应的生命周期
        binding.lifecycleOwner = this
        binding.button.setOnClickListener {
            userLiveData.value= User("李", "san")
        }
    }
    companion object{
        private const val TAG = "MainActivity"
    }
}