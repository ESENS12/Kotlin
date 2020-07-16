package kr.esens.searchnblogwithoutads

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Query


class MainActivity : AppCompatActivity() {
    companion object{
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        my_webview.webViewClient = WebViewClient()
//        cl_no_data.visibility = View.INVISIBLE;

        btn_search.setOnClickListener{
            Log.i(TAG,"Execute Search! : " + et_searchQuery.text);
            executeSearch(et_searchQuery.text.toString())
        }

    }

    fun executeSearch(searchQuery : String){
        /*
        *  임시 테스트용 변수 생성
        *
        * */
        var searchOption = "sim";
        var page = 1;
        cl_no_data.visibility = View.INVISIBLE;

        SearchRetrofit.getService().requestBlogList(query = searchQuery, st=searchOption, start = page).enqueue(object :
            Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "res Failure : $t");
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d(TAG, "onResponse  : $response" );
//                val inputStream = response.body()?.byteStream()
//                val bitmap = BitmapFactory.decodeStream(inputStream);
//                iv_image.setImageBitmap(bitmap);
            }
        })

        //webview test
//        my_webview.visibility = View.VISIBLE;
//        my_webview.loadUrl("https://search.naver.com/search.naver?query=${searchQuery}&sm=tab_pge&srchby=all&st=${searchOption}&where=post&start=${page}")



    }
}