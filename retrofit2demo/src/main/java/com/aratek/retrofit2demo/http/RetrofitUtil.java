package com.aratek.retrofit2demo.http;

import android.annotation.SuppressLint;
import android.util.Log;


import com.aratek.retrofit2demo.BuildConfig;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @ClassName RetrofitUtil
 * @Description 网络请求工具类
 * @Author nick
 * @Date 2021/10/9 14:03
 * @Version 1.0
 */
public class RetrofitUtil {
    private static final String TAG = "RetrofitUtil";
    public WebService service;
    private static String baseUrl = "https://www.wanandroid.com/";

    private RetrofitUtil() {
        service = new Retrofit.Builder().baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getUnsafeOkHttpClient())
                .build().create(WebService.class);

    }


    public static RetrofitUtil getInstance() {

        return RetrofitUtilInner.instance;

    }


    private static class RetrofitUtilInner {

        private static final RetrofitUtil instance = new RetrofitUtil();

    }

    public static OkHttpClient getUnsafeOkHttpClient() {

        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] chain,
                                String authType) {
                            //Do nothing
                        }

                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] chain,
                                String authType) {
                            //Do nothing
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[0];
                        }
                    }};
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts,
                    new java.security.SecureRandom());

            final SSLSocketFactory sslSocketFactory = sslContext
                    .getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory)
                    .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            builder.connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS);

            if (BuildConfig.DEBUG) {
            Log.d(TAG, "getUnsafeOkHttpClient: 网络请求日志");
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
                Log.d("HttpLogInfo", message);//okHttp的详细日志会打印出来
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
            }
            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    public  static  RequestBody buildRequest(JSONObject jsonObject)  {
//        initParam(jsonObject);
        return RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), jsonObject.toString());
    }

//    public String parseError(Throwable throwable) {
//        if (throwable instanceof ConnectException){
//            LogUtils.e(((ConnectException)throwable).getMessage());
//            return "网络异常，请检查您的网络！";
//        } else if (throwable instanceof HttpException) {
//            LogUtils.e(((HttpException)throwable).code() + ((HttpException)throwable).message() + ((HttpException)throwable).response());
////            return "请求失败，请重试！";
//        }
//        return "请求失败，请重试！";
//    }
}
