package com.aratek.retrofit2demo.repositorys

import com.aratek.retrofit2demo.http.HttpResult
import com.aratek.retrofit2demo.http.RetrofitUtil
import com.aratek.retrofit2demo.models.User

/**
 * @ClassName LoginRepository
 * @Description TODO
 * @Author nick
 * @Date 2021/11/25 17:22
 * @Version 1.0
 */
class LoginRepository :BaseRepository() {

   suspend fun login(userName:String,password:String):HttpResult<User?>{
      return  safeApiCall { RetrofitUtil.getInstance().service.login(userName,password) }
    }

}