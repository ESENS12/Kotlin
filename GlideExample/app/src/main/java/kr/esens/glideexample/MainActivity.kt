package kr.esens.glideexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var bIsSuccess : Boolean = false;

        //이미지 요청 성공
        val success_url : String = "https://postfiles.pstatic.net/MjAxOTA4MjdfOTEg/MDAxNTY2OTAzMjUyNDY4.4Wd8GH8BY7_24v5YTPiuGWhVN9Rq4F_8-bhWjnnHc-cg.iL_rn_If5OB0MEEiD5-Sw2TSYpmMFdk34Ck6vZVmOMQg.GIF.haji31/2019-08-27-19-38-18_1.gif?type=w966";

        //이미지 요청 실패 url
        val failed_url : String = "http://goo.gl/gEgYUd";

        //gif도 별다른 핸들링이 필요없음.
        btn_getImage.setOnClickListener {
            var load_url : String = ""

            //성공 , 실패 이미지 번갈아가면서 보여주기 위함
            if(bIsSuccess){
                load_url = failed_url
                bIsSuccess = false;
            }else{
                load_url = success_url
                bIsSuccess = true;
            }

            Glide.with(this).load(load_url)
//                    .override(이미지 사이즈) //만약 리소스 조정이 필요하다면, 캐시낭비를 막는데 효과적임
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.image_not_found)
                    .into(iv_image)
        }

    }
}