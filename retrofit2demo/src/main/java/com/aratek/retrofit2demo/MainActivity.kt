package com.aratek.retrofit2demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.aratek.retrofit2demo.databinding.ActivityMainBinding
import com.aratek.retrofit2demo.http.RetrofitUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this,R.layout.activity_main)
        RetrofitUtil.getInstance().service.login("865986969","Sign682078").enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                //注意这里不是toString
                binding.tvRespBody.text = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                binding.tvRespBody.text =t.message
            }
        })
//        lifecycleScope.launch(Dispatchers.IO){
//              val rep=  RetrofitUtil.getInstance().service.login("865986969","Sign682078").execute()
//            withContext(Dispatchers.Main){
//                binding.tvRespBody.text = rep.toString()
//            }
//        }

        val requestBody = RequestBody.create(MediaType.parse("application/json"),"{\"username\":\"856986969\",\"password\":\"Sign682078\"}")
        RetrofitUtil.getInstance().service.login2(requestBody).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                //注意这里不是toString
                Log.d(TAG, "onResponse: JSON")
                binding.tvRespBody.text = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                binding.tvRespBody.text =t.message
            }
        });
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}