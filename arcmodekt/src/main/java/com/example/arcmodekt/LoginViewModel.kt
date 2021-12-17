package com.example.arcmodekt

import androidx.lifecycle.*

import com.example.arcmodekt.http.Resource
import com.example.arcmodekt.model.BaseBean
import com.example.arcmodekt.model.User
import com.example.arcmodekt.repository.LoginRepository

/**
 * @ClassName LoginViewModel
 * @Description TODO
 * @Author nick
 * @Date 2021/11/25 17:11
 * @Version 1.0
 */
class LoginViewModel : ViewModel() {
    companion object {
        private const val TAG = "LoginViewModel"
    }

    val loginRepository by lazy {
        LoginRepository()
    }


     fun login(userName: String, password: String): LiveData<Resource<User>> {
      return  loginRepository.login2(userName,password)
    }

}