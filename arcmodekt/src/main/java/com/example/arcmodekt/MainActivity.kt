package com.example.arcmodekt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.arcmodekt.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this,R.layout.activity_main)
        //创建ViewModel
        val model: LoginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        model.login("865986969","Sign682078").observe(this, Observer {
            val aa = it;
            Log.d(TAG, "onCreate: "+aa)
        })
        binding.tvRespBody.setOnClickListener {
            model.user.observe(this, Observer {
                binding.tvRespBody.text = it.toString()
            })
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}