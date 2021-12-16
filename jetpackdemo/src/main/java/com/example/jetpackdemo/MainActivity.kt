package com.example.jetpackdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.jetpackdemo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        // Inflate view and obtain an instance of the binding class.
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Specify the current activity as the lifecycle owner.
//        binding.lifecycleOwner = this

        val user = User("张","san")

//        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
//        viewModel.liveDataUser.value = user
//        binding.viewModel = viewModel
        binding.user = user
        user.lastName   = "li"


    }
}