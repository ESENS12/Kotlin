package kr.esens.jsoupexample

import android.os.Bundle
import android.util.Log
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class MainActivity : AppCompatActivity() {

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)
    private val TAG  = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        coroutineScope.launch(Dispatchers.IO) {
            val url : String = "https://search.naver.com/search.naver?where=post&st=sim&query=장호항&start=1&srchby=all&tab_pge=tab_pge"
            val res : Document? = getHtmlDocument(url)
//            Log.i(TAG , "res : $res")

//            el#id ☞ id로 가지고 오기
//            el.class ☞ class로 가지고 오기
//            el[attr] ☞ attribute로 가지고 오기
            val elem: Elements? = res?.select("a.sh_blog_title")

            elem?.forEach {
                Log.d(TAG,"item : ${it.attr("href")}")
            }
        }

    }

    @UiThread
    private fun getHtmlDocument(url : String): Document? {
        val response: Connection.Response = Jsoup.connect(url)
            .method(Connection.Method.GET)
            .execute()
        return response.parse();
    }

}




