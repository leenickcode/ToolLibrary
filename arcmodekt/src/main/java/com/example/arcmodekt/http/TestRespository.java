package com.example.arcmodekt.http;





import com.example.arcmodekt.model.User;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TestRespository {
    public TestRespository() {


    }

    public LiveData<Resource<User>> getStrategy(String userName , String  password){

        return  new NetworkBoundResource<User,User>(){

            @Override
            protected void saveCallResult(User item) {

            }

            @Override
            protected Boolean shouldFetch(User data) {
                return true;
            }

            @Override
            protected LiveData<User> loadFromDb() {
                return new MutableLiveData<User>();
            }

            @Override
            protected LiveData<HttpResult<User>> createCall() {
                return RetrofitUtil.getInstance().service.login2(userName,password);
            }
        }.asLiveData();
    }
}
