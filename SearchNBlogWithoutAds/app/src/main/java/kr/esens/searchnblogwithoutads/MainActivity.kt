package kr.esens.searchnblogwithoutads

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
        private lateinit var mContext : Context;
        private var page = 1;
    }

    private val nBlog_img_tag = "div.se-module.se-module-image > a > img[src]" // naver blog 이미지 태그
    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)
    private var ar_blogimgList = ArrayList<String>();
    private var ar_blogList = ArrayList<BlogItem>();
    private var blogAdapter: BlogAdapter? = null;
    private var mBackWait:Long = 0

    override fun onBackPressed() {

        // 뒤로가기 버튼 클릭
        if(my_webview!!.visibility == View.VISIBLE){
            my_webview.visibility = View.GONE;
            return;
        }

        if(System.currentTimeMillis() - mBackWait >=2000 ) {
            mBackWait = System.currentTimeMillis()
            Toast.makeText(mContext,"뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        } else {
            finish() //액티비티 종료
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext = this.applicationContext;

        my_webview.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                url: String?
            ): Boolean {
                view?.loadUrl(url)
                return true;
//                return super.shouldOverrideUrlLoading(view, request)
            }
        }

        val mLinearLayoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false);

        my_webview.webViewClient = WebViewClient()

        blogAdapter = BlogAdapter(mContext, ar_blogList);
        blogAdapter!!.setOnItemClickListener(object : BlogAdapter.OnItemClickListener {
            override fun onItemClick(v: View, position: Int) {
                my_webview.visibility=View.VISIBLE;
                my_webview!!.loadUrl(ar_blogList[position].BlogUrl);
//                Log.e(TAG,"onItemClick(main): $position" )
            }
        })
        btn_searchMore.setOnClickListener {
            searchMore();
        }

        btn_search.setOnClickListener {
            executeSearch(et_searchQuery.text.toString(),true)
        }

        recycler_view.apply {
            this.adapter = blogAdapter
            this.layoutManager = mLinearLayoutManager
            this.isNestedScrollingEnabled = true
            this.setHasFixedSize(true)
        }

        et_searchQuery.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                executeSearch(et_searchQuery.text.toString(),true)
                return@OnKeyListener true
            }
            false
        })
    }

    fun searchMore(){
        page += 10
        executeSearch(et_searchQuery.text.toString(),false)
    }

    fun executeSearch(searchQuery: String, isNewSearch : Boolean) {

        var searchOption = "sim";

        //신규 검색일때만 기존 리스트 초기화(검색버튼 클릭)
        if(isNewSearch){
            ar_blogList.clear();
            page = 1;
        }
        //todo 클릭 이벤트 제목만 가능하게 수정 , 백버튼 시 웹뷰 부터 닫게끔 수정, 멀티쓰레드 작업

        cl_no_data.visibility = View.GONE;
//        LoadingDialog(this).show()
        val dialog = LoadingDialog(this@MainActivity)
        dialog.show()

        SearchRetrofit.getService()
            .requestBlogList(query = searchQuery, st = searchOption, start = page).enqueue(object :
            Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "res Failure : $t")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val str_res: String = response.body()?.string().toString()

                coroutineScope.launch(Dispatchers.IO) {
                    val res: Document? = Jsoup.parse(str_res)
                    val elem: Elements? = res?.select("a.sh_blog_title")

                    elem?.forEachIndexed() { index, it ->

                        var blogItem = BlogItem();
                        //블로그 url 추출
                        val blog_url = parseBlogUri(it.attr("href"))
                        blogItem.BlogUrl = blog_url;
                        blogItem.BlogImages = ArrayList<String>();
                        blogItem.PostTitle = it.attr("title");

                        Log.d(TAG, "blog_url : $blog_url")

                        //해당 블로그에서 이미지 태그 uri 추출
                        val elem_item: Elements? = getImageUriFromBlog(blog_url);
                        if (elem_item != null) {
                            Log.e(TAG,"elem_item.length ${elem_item.size}")
                        }
                        val myClassifier = Classifier()
                        elem_item?.forEach {
                            val str_res = it.attr("src").replace("w80_blur" , "w800");
                            val bIsFakeBlog = myClassifier.Classification(str_res)
//                            Log.d(TAG,"imgUrl : $str_res");
                            blogItem.bIsFakeBlog = bIsFakeBlog;
                            blogItem.BlogImages?.add(str_res);
                        }

                        Log.d(TAG, "====================$index=======================")
                        if(blogItem.BlogImages?.size!! >0){
                            ar_blogList.add(blogItem)
                        }
                    }

                    async {

                        coroutineScope.launch (Dispatchers.Main){
//                            LoadingDialog(mContext).dismiss()
                            dialog.dismiss()
                            blogAdapter?.notifyDataSetChanged();
                        }

                    }.await()

                }

            }
        })
    }

    @UiThread
    private fun getImageUriFromBlog(url: String): Elements? {
        val response: Connection.Response;

        try {
            response = Jsoup.connect(url)
                .method(Connection.Method.GET)
                .execute()
        } catch (e: Exception) {
            e.printStackTrace();
            Log.e(TAG, "ERROR  : $e");
            return null
        }
        val item = response.parse()
        val img_tag = item.select(nBlog_img_tag);

        return if (img_tag.size == 0){
            item.select("div.se-section.se-section-image > a > img[src]")
        }else{
            img_tag
        }

    }

    //blog.me로 끝나는 naver blog url을 blog.naver.com으로 변경해줘야함
    private fun parseBlogUri(url: String): String {
        if (url.indexOf("blog.me") > -1) {
            val nickName = url.substring(url.indexOf("/") + 2, url.indexOf("."))
            val postNumber = url.substring(url.lastIndexOf("/") + 1)
            // https://m.blog.naver.com/conanronse?Redirect=Log&logNo=221607525803
            return "https://m.blog.naver.com/${nickName}?Redirect=Log&logNo=${postNumber}"
        } else {
            return url.replace("https://", "https://m.")
        }
    }
}