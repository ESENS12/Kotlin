package kr.esens.searchnblogwithoutads

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object SearchRetrofit {

    fun getService(): RetrofitService = retrofit.create(RetrofitService::class.java)

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofit =
        Retrofit.Builder()
            .baseUrl("https://search.naver.com")
//            .baseUrl("https://mblogthumb-phinf.pstatic.net")
            .addConverterFactory(GsonConverterFactory.create(gson)) // GSON ConverterFactory 생성 및 전달
            .build()

}