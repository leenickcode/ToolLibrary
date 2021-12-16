package com.example.arcmodekt.repository


import com.blankj.utilcode.util.LogUtils
import com.example.arcmodekt.http.HttpResult
import retrofit2.HttpException
import java.net.UnknownHostException

/**
 * @ClassName BaseRepository
 * @Description TODO
 * @Author nick
 * @Date 2021/11/25 17:28
 * @Version 1.0
 */
 abstract class BaseRepository {
    /**
     * 安全的请求，retrofit在kt里面只要服务器响应的code不是200，直接抛HttpException异常。所以要抓一下
     */
    suspend fun <T : Any?> safeApiCall(call: suspend () -> HttpResult<T>): HttpResult<T> {
        return try {
            call()
        } catch (e: Exception) {
            LogUtils.e("请求服务器异常：code:$${e.message}")
            e.printStackTrace()
            if (e is HttpException){

                LogUtils.e("code+${e.code()}---msg: ${e.message()}---response: ${
                    e.response().toString()
                }")
                HttpResult<T>().also {
                    LogUtils.d(e.code(),e.message())
                    when(e.code()){
                        in 401..499->{
                            it.message  = "客户端请求异常"
                        }
                        in 500..599->{
                            it.message  = "服务器异常"
                        }
                    }
                    it.code = e.code()
                }
            }else if (e is UnknownHostException){
                HttpResult<T>().apply {
                    message = "无法连接网络"
                    code=-1

                }
            } else{
                HttpResult<T>().apply {
                    message = "请求服务器超时（可能网络信号不好），请稍后再试。。。"
                    code=-1

                }
            }
        }
    }
}