package com.example.arcmodekt.http

import androidx.lifecycle.LiveData
import com.example.arcmodekt.model.User

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * @ClassName WebService
 * @Description TODO
 * @Author nick
 * @Date 2021/11/26 10:34
 * @Version 1.0
 */
interface WebService {
    //    @FormUrlEncoded
//    @POST("user/login")
//    fun login(@Field("username") username:String,@Field("password") password:String):Call<HttpResult<User>>
    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): HttpResult<User?>

    @FormUrlEncoded
    @POST("user/login")
     fun login2(
        @Field("username") username: String,
        @Field("password") password: String
    ): LiveData<HttpResult<User?>>

}