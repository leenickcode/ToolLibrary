package com.aratek.retrofit2demo.view_model

import android.util.Log
import androidx.lifecycle.*
import com.aratek.retrofit2demo.models.User
import com.aratek.retrofit2demo.repositorys.LoginRepository
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

     fun login(userName: String, password: String) {
            viewModelScope.launch(Dispatchers.IO) {
                Log.d(TAG, "login: " + Thread.currentThread().name)
                val userTemp= loginRepository.login(userName, password).data
                userTemp?.let {
                    user.postValue(it)
                }

            }

    }
}