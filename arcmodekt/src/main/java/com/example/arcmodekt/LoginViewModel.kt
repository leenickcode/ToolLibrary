package com.example.arcmodekt

import android.util.Log
import androidx.lifecycle.*

import com.example.arcmodekt.http.Resource
import com.example.arcmodekt.http.TestRespository
import com.example.arcmodekt.model.User
import com.example.arcmodekt.repository.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

//    val user: LiveData<User> = liveData {
//        val data = database.loadUser() // loadUser is a suspend function.
//        emit(data)
//    }

    val  user:MutableLiveData<User> = MutableLiveData()
    val loginRepository by lazy {
        LoginRepository()
    }


     fun login(userName: String, password: String): LiveData<Resource<User>> {
//            viewModelScope.launch(Dispatchers.IO) {
//                Log.d(TAG, "login: " + Thread.currentThread().name)
//                val userTemp= loginRepository.login(userName, password).data
//                userTemp?.let {
//                    user.postValue(it)
//                }
//
//            }
      return  TestRespository().getStrategy(userName,password)
    }


}