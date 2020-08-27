package kr.esens.searchnblogwithoutads

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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
        private var clearHistory = false;
    }


    //디버깅 모드 (자동검색)
    private val IS_DEBUUGING_MODE = false;
    private var DEBUGGING_SEARCH_QUERY = "노원구 맛집";

    private val nBlog_img_tag = "div.se-module.se-module-image > a > img[src], div.se-section.se-section-image > a > img[src], div.se-imageStrip-container > a > img[src]" // naver blog 이미지 태그
    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)
    private var ar_blogimgList = ArrayList<String>();
    private var ar_blogList = ArrayList<BlogItem>();
    private var blogAdapter: BlogAdapter? = null;
    private var mBackWait:Long = 0
    private var loadingFinished = true
    private var redirect = false

    override fun onBackPressed() {

        // 뒤로가기 버튼 클릭
        if(my_webview!!.visibility == View.VISIBLE){
//            val list = my_webview.copyBackForwardList();

            if(my_webview.canGoBackOrForward(-2)){
                my_webview.goBack();
                return
            }

            clearHistory = true
            my_webview.visibility = View.GONE;
            my_webview.loadUrl("about:blank");
            return
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

        my_webview.settings.javaScriptEnabled = true
        my_webview.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                url: String?
            ): Boolean {

                if (!loadingFinished) {
                    redirect = true;
                }

                loadingFinished = false;

                view?.loadUrl(url)
                return true
//                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {

                super.onPageStarted(view, url, favicon)
                loadingFinished = false

            }
            override fun onPageFinished(view: WebView?, url: String?) {
//                Log.e(TAG,"onPageFinished!")
                if (!redirect) {
                    loadingFinished = true;

                    if(clearHistory){
                        Log.e(TAG,"onPageFinished, clear History called");
                        view?.clearHistory()
                        clearHistory = false
                    }
                } else {
                    redirect = false;
                }

                super.onPageFinished(view, url)
            }
        }

        val mLinearLayoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false);

//        my_webview.webViewClient = WebViewClient()

        blogAdapter = BlogAdapter(mContext, ar_blogList);
        blogAdapter!!.setOnItemClickListener(object : BlogAdapter.OnItemClickListener {
            override fun onItemClick(v: View, position: Int) {
                my_webview!!.loadUrl(ar_blogList[position].BlogUrl);
                my_webview.visibility = View.VISIBLE;
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

        //디버깅 모드(자동검색)
        if(IS_DEBUUGING_MODE){
            et_searchQuery.text = DEBUGGING_SEARCH_QUERY.toEditable();
            executeSearch(et_searchQuery.text.toString(), true);
        }
    }

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

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
                            //todo  w966 check 하려고 했으나, 로딩 전에는 w80_blur 이므로 불가능해보임..
//                            if(it.attr("src").contains("bitna2020")){
////                                it.
//                                Log.e(TAG,"str_res = ${it.attr("src")}")
//                            }
                            if(it.attr("src").contains("type=w966")){
                                Log.e(TAG,"find w966!");
                            }
                            val bIsFakeBlog = myClassifier.Classification(it.attr("src"))

                            var str_res = it.attr("src").replace("w80_blur" , "w800");
                            str_res = str_res.replace("type=w966" , "type=w800");
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
//        item.select("div.se-section.se-imageStrip-container > a > img[src]")
        //section + 일반이미지로 되어야하는데..

//        return if (img_tag.size == 0){
//            item.select("div.se-section.se-section-image > a > img[src]")
//        }else{
//            img_tag
//        }

        return img_tag

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