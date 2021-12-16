package com.example.arcmodekt.repository

import com.example.arcmodekt.http.HttpResult
import com.example.arcmodekt.http.RetrofitUtil
import com.example.arcmodekt.model.User


/**
 * @ClassName LoginRepository
 * @Description TODO
 * @Author nick
 * @Date 2021/11/25 17:22
 * @Version 1.0
 */
class LoginRepository :BaseRepository() {

   suspend fun login(userName:String,password:String): HttpResult<User?> {
      return  safeApiCall { RetrofitUtil.getInstance().service.login(userName,password) }
    }

}