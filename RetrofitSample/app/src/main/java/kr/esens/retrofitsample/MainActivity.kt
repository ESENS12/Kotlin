package kr.esens.retrofitsample

import android.graphics.BitmapFactory
import android.media.Image
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    companion object{
        const val TAG = "MainActivity"

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //get HTML Sample
//        SearchRetrofit.getService().requestBlogList(query = "서울대입구 맛집", st = "sim", start = 1).enqueue(object :
//            Callback<ResponseBody> {
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                Log.e(TAG, "res Failure : $t");
//            }
//
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                Log.d(TAG, "onResponse length : " + response.body()?.string());
//                //첫번째 줄 외에는 이상하게 표현이 되어버림, assign 한 후 작업
//                Log.d(TAG, "" + response.body()?.string()?.reversed());
//            }
//        })


        search_button.setOnClickListener{
            SearchRetrofit.getService().requestSearchImage(query = "MDAxNTkzMTQ5NDg3MzQ0.cbRzmTi6r77_p6sWpmBj753HrR6VbzI4WCwkYMkLFn8g.qCEDdrC6-bblRFrGVDqqmk8fpJlsVJ5WcVyJd59OxBMg.JPEG.limmmm22",sub="SE-631feb29-32d5-4d6b-94c3-9a203b318af3.jpg").enqueue(object :
                Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e(TAG, "res Failure : $t");
                }

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.d(TAG, "onResponse  : $response" );
                    val inputStream = response.body()?.byteStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream);
                    iv_image.setImageBitmap(bitmap);
                }
            })
        }


    }
}