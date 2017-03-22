package com.azstudio.customwidget;


import com.azstudio.model.ShanbayWord;
import com.azstudio.model.WordInfo;


import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by YKA-SFB on 2017/3/14.
 */

public interface ApiService {
    //获取新闻请求，返回Observable对象
    @GET("bdc/search")
    Call<WordInfo> getWordInfo(@Query("word") String word);
}
