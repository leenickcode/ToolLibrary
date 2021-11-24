package com.aratek.retrofit2demo.http;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @ClassName WebService
 * @Description
 * @Author nick
 * @Date 2021/10/9 14:10
 * @Version 1.0
 */
public interface WebService {

    @POST("guard/standard/gotUserInfo")
     Call<HttpResult<String>> gotUser(@Body RequestBody requestBody);

    @POST("guard/standard/getUserInfo")
     Call<HttpResult<String>> getUserInfo(@Body RequestBody requestBody);

    @POST("guard/standard/backStatus")
    Call<HttpResult<String>> backResult(@Body RequestBody requestBody);


    /**
     * 表达请求登录
     * @param username  用户名
     * @param password  密码
     * @return
     */
    @FormUrlEncoded
    @POST("user/login")
    Call<ResponseBody> login(@Field("username") String username, @Field("password") String password);
    /**
     * json方式
     * @return
     */

    @POST("user/login")
    Call<ResponseBody> login2(@Body RequestBody info);
}
