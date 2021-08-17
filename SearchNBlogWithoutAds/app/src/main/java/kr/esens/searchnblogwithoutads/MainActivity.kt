package kr.esens.searchnblogwithoutads

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
import kotlin.system.measureTimeMillis


class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
        private lateinit var mContext: Context
        private var page = 1
        private var clearHistory = false
        private lateinit var mDialog: Dialog

        private lateinit var mHandler: Handler
        private lateinit var mRunnable: Runnable
    }

    //디버깅 모드 (자동검색)
    private val IS_DEBUUGING_MODE = true;
    private var DEBUGGING_SEARCH_QUERY = "을지로 맛집";
    private var requestSearchMore = false;
    private var beforeSrollState = 0;
    //시간 측정용
    private val IS_CHECK_TIME_MODE = true;

    private val nBlog_img_tag =
        "div.se-module.se-module-image > a > img[src], div.se-section.se-section-image > a > img[src], div.se-imageStrip-container > a > img[src]" // naver blog 이미지 태그
    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)
    private var ar_blogList = ArrayList<BlogItem>();
    private var blogAdapter: BlogAdapter? = null;
    private var mBackWait: Long = 0
    private var loadingFinished = true
    private var redirect = false

    override fun onBackPressed() {

        // 뒤로가기 버튼 클릭
        if (my_webview!!.visibility == View.VISIBLE) {
            if (my_webview.canGoBackOrForward(-2)) {
                my_webview.goBack();
                return
            }
            clearHistory = true
            my_webview.visibility = View.GONE;
            my_webview.loadUrl("about:blank");
            return
        }

        if (System.currentTimeMillis() - mBackWait >= 2000) {
            mBackWait = System.currentTimeMillis()
            Toast.makeText(mContext, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        } else {
            finish() //액티비티 종료
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mContext = this.applicationContext
        mDialog = LoadingDialog(this)
        mHandler = Handler()

        swipe.setOnRefreshListener {

            mRunnable = Runnable {
                swipe.isRefreshing = false
            }
            executeSearch(et_searchQuery.text.toString(), true);
            mHandler.postDelayed(mRunnable, 5000)
        }

        cl_bottomView.visibility = View.GONE
        my_webview.settings.javaScriptEnabled = true
        my_webview.webViewClient = object : WebViewClient() {
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

                    if (clearHistory) {
                        Log.e(TAG, "onPageFinished, clear History called");
                        view?.clearHistory()
                        clearHistory = false
                    }
                } else {
                    redirect = false;
                }

                super.onPageFinished(view, url)
            }
        }

        val mLinearLayoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);

        blogAdapter = BlogAdapter(mContext, ar_blogList);
        blogAdapter!!.setOnItemClickListener(object : BlogAdapter.OnItemClickListener {
            override fun onItemClick(v: View, position: Int) {
                my_webview!!.loadUrl(ar_blogList[position].BlogUrl);
                my_webview.visibility = View.VISIBLE;
//                Log.e(TAG,"onItemClick(main): $position" )
            }
        })

        //not used.. 자동 검색으로 변경
        //더보기 버튼 클릭
//        iv_searchmore.setOnClickListener {
//            searchMore()
//        }

        //검색 버튼 클릭
        btn_search.setOnClickListener {
            executeSearch(et_searchQuery.text.toString(), true)
        }

        recycler_view.apply {
            this.adapter = blogAdapter
            this.layoutManager = mLinearLayoutManager
            this.isNestedScrollingEnabled = true
            this.setHasFixedSize(true)
            this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    Log.e(TAG,"newState : $newState")
                    if (!canScrollVertically(1)) {
                        if(requestSearchMore){
                            searchMore()
                        }
                        if(newState == 0){
                            requestSearchMore = !requestSearchMore;
                        }
                    }
                }
            })
        }

        et_searchQuery.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                executeSearch(et_searchQuery.text.toString(), true)
                return@OnKeyListener true
            }
            false
        })

        //디버깅 모드(자동검색)
        if (IS_DEBUUGING_MODE) {
            et_searchQuery.text = DEBUGGING_SEARCH_QUERY.toEditable();
            executeSearch(et_searchQuery.text.toString(), true);
        }
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    fun searchMore() {
        page += 10
        executeSearch(et_searchQuery.text.toString(), false)
    }

    fun executeSearch(searchQuery: String, isNewSearch: Boolean) {
        var searchOption = "sim";

        //신규 검색일때만 기존 리스트 초기화(검색버튼 클릭)
        if (isNewSearch) {
            ar_blogList.clear();
            page = 1;
        }

        cl_no_data.visibility = View.GONE;

        showNhide_dialog(true);

        coroutineScope.launch(Dispatchers.IO) {
            SearchRetrofit.getService()
                .requestBlogList(query = searchQuery, st = searchOption, start = page)
                .enqueue(object :
                    Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e(TAG, "res Failure : $t")
                    }

                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        val str_res: String = response.body()?.string().toString()
                        if (str_res.equals("")) {
                            Log.e(TAG, "str_res is null");
                            return;
                        }
                        // 여기서 str_res 를 parsing 하고, url까지만 추출 한 다음, url과 index를 가지고 digging more 요청은 subsequential하게 수정
                        val elapsed: Long = measureTimeMillis {
                            val res: Document? = Jsoup.parse(str_res)
                            val elem: Elements? = res?.select("div.total_wrap a.api_txt_lines");
                            var blogItemList = Array<BlogItem>(30) { BlogItem() };
//                            Log.e(TAG,"elem.size : ${elem?.size}")

                            elem?.forEachIndexed() { index, it ->

                                var blogItem = BlogItem();
                                //블로그 url 추출
                                val blog_url = parseBlogUri(it.attr("href"))
                                blogItem.BlogUrl = blog_url;
                                blogItem.BlogImages = ArrayList<String>();
                                blogItem.PostTitle = it.text()

//                                Log.d(TAG, "blog_url : $blog_url");
//                                Log.d(TAG, "PostTitle[raw] : " + it.text());

                                blogItemList[index] = (blogItem);
                            }

                            basic_sequential(blogItemList);
                        }

                        if (IS_CHECK_TIME_MODE) {
                            Log.e(TAG, "total elapsed time : $elapsed ms");
                        }

                    }

                })
        }
    }

    //    @UiThread
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

        return img_tag

    }

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

    suspend fun digging_more_for_images(blogItem: BlogItem): BlogItem {
        val elem_item: Elements? = getImageUriFromBlog(blogItem.BlogUrl);
        if (elem_item != null) {
            Log.e(TAG, "1__elem_item.length ${elem_item.size}, url : ${blogItem.BlogUrl}")
        }
        val myClassifier = Classifier()
        elem_item?.forEach {

            val bIsFakeBlog = myClassifier.Classification(it.attr("src"))

            var str_res = it.attr("src").replace("w80_blur", "w800");
            str_res = str_res.replace("type=w966", "type=w800");
            //                            Log.d(TAG,"imgUrl : $str_res");
            blogItem.bIsFakeBlog = bIsFakeBlog;
            if (blogItem.BlogImages?.size!! < 10) {
                blogItem.BlogImages?.add(str_res);
            }
        }
        return blogItem
    }

    fun basic_sequential(blogList: Array<BlogItem>) = runBlocking {
        launch(Dispatchers.IO) {

            var blogItem1: BlogItem
            var blogItem2: BlogItem
            var blogItem3: BlogItem
            var blogItem4: BlogItem
            var blogItem5: BlogItem

            val time = measureTimeMillis {
                for (i in 1..2) {
                    val one =
                        async(start = CoroutineStart.LAZY) {
                            digging_more_for_images(blogList[i])
                        }
                    val two =
                        async(start = CoroutineStart.LAZY) {
                            digging_more_for_images(blogList[i+2])
                        }
                    val three =
                        async(start = CoroutineStart.LAZY) {
                            digging_more_for_images(blogList[i+4])
                        }

                    val four =
                        async(start = CoroutineStart.LAZY) {
                            digging_more_for_images(blogList[i+6])
                        }

                    val five =
                        async(start = CoroutineStart.LAZY) {
                            digging_more_for_images(blogList[i+8])
                        }

                    one.start()
                    two.start()
                    three.start()
                    four.start()
                    five.start()

                    blogItem1 = one.await()
                    blogItem2 = two.await()
                    blogItem3 = three.await()
                    blogItem4 = four.await()
                    blogItem5 = five.await()

                    ar_blogList.add(blogItem1)
                    ar_blogList.add(blogItem2)
                    ar_blogList.add(blogItem3)
                    ar_blogList.add(blogItem4)
                    ar_blogList.add(blogItem5)
                }
            }

            Log.e(TAG, "digging more is completed in $time ms")

            withContext(Dispatchers.IO) {

                coroutineScope.launch(Dispatchers.Main) {
                    showNhide_dialog(false)
                    blogAdapter?.notifyDataSetChanged()
                }

            }

        }

    }

    @UiThread
    fun showNhide_dialog(isShow: Boolean) {
        if (isShow) {
            mDialog.show()
        } else {
            mDialog.hide()
        }
    }

}