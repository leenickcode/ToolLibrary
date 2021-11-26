package com.aratek.retrofit2demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aratek.retrofit2demo.databinding.ActivityMainBinding
import com.aratek.retrofit2demo.view_model.LoginViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this,R.layout.activity_main)
        //创建ViewModel
        val model: LoginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        model.login("865986969","Sign682078")
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