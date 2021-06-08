package kr.esens.searchnblogwithoutads

import android.media.Image
import android.text.Html
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {

    //1차-> 검색결과 블로그 url 수집
    @GET("search.naver")
    fun requestBlogList(
        @Query("query") query: String,
        @Query("tab_pge") tab_pge: String = "tab_pge",
        @Query("srchby") srchby: String = "all",
        @Query("st") st: String,
        @Query("display") display: Int = 10,
        @Query("where") where: String = "post",
        @Query("start") start: Int = 1
    ): Call<ResponseBody>


    //최종 이미지 다운로드
    @GET("{sb}/{query}/{sub}")
    @Streaming  //용량이 작을때는 skip 가능
    fun requestSearchImage(
        @Path("sb") sb: String = "MjAyMDA2MjZfMTcg",
        @Path("query") query: String,
        @Path("sub") sub: String,
        @Query("type") type: String = "w800"

    ): Call<ResponseBody>
}