package com.example.jetpackdemo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel :ViewModel() {
    val liveDataUser =MutableLiveData<User>()
    val name = MutableLiveData<String>()

}