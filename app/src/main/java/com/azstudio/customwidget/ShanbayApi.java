package com.azstudio.customwidget;

import com.azstudio.model.WordInfo;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by YKA-SFB on 2017/3/14.
 */

public class ShanbayApi {
    private String baseUrl = "https://api.shanbay.com/";
    static Retrofit retrofit;
    private static ApiService apiService;

    public ShanbayApi() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(this.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                //添加Rx适配
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public static ApiService getDefault() {
        if (apiService == null) {
            synchronized (ShanbayApi.class) {
                if (apiService == null) {
                    new ShanbayApi();
                }
            }
        }
        return ShanbayApi.apiService;
    }
}
