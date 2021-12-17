package com.example.arcmodekt.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.arcmodekt.MyApplication
import com.example.arcmodekt.database.RoomDataBase
import com.example.arcmodekt.http.*
import com.example.arcmodekt.model.BaseBean
import com.example.arcmodekt.model.User
import java.util.concurrent.TimeUnit


/**
 * @ClassName LoginRepository
 * @Description TODO
 * @Author nick
 * @Date 2021/11/25 17:22
 * @Version 1.0
 */
class LoginRepository :BaseRepository() {

    private val repoListRateLimit = RateLimiter<String>(10, TimeUnit.MINUTES)
    var user :MutableLiveData<User> = MutableLiveData();

    fun login2(userName: String?, password: String?): LiveData<Resource<User>> {
        return object : NetworkBoundResource<User,BaseBean<User>>() {

            override fun loadFromDb(): LiveData<User> {
                return RoomDataBase.getInstance(MyApplication.instance).userDao().findUser("118298")
            }


            override fun shouldFetch(data:User?): Boolean {
             return  data==null
            }

            override fun saveCallResult(item: BaseBean<User>) {
                RoomDataBase.getInstance(MyApplication.instance).userDao().insertUser(item.data)
            }

            override fun createCall(): LiveData<ApiResponse<BaseBean<User>>> {
               return  RetrofitUtil.getInstance().service.login2(
                    userName!!,
                    password!!
                )
            }
        }.asLiveData()
    }
}