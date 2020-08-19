package kr.esens.searchnblogwithoutads

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.WebViewClient
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import java.lang.Exception


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

//    private val blogAdapter = BlogAdapter(ar_blogList);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext = this.applicationContext;
        val mLinearLayoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false);

        my_webview.webViewClient = WebViewClient()

        blogAdapter = BlogAdapter(mContext, ar_blogList);

        btn_searchMore.setOnClickListener {
            searchMore();
        }

        btn_search.setOnClickListener {
            Log.i(TAG, "Execute Search! : " + et_searchQuery.text);
            executeSearch(et_searchQuery.text.toString())
        }

        recycler_view.apply {
            this.adapter = blogAdapter
            this.layoutManager = mLinearLayoutManager
//            this.addItemDecoration(
//                DividerItemDecoration(
//                    mContext,
//                    LinearLayoutManager.VERTICAL
//                )
//            )
            this.isNestedScrollingEnabled = true
            this.setHasFixedSize(true)
        }

//        setRecyclerViewScrollListener();

        et_searchQuery.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                executeSearch(et_searchQuery.text.toString())
                return@OnKeyListener true
            }
            false
        })
    }

    //todo  set recycler view scroll listener!
//
//    private fun setRecyclerViewScrollListener() {
//        scrollListener = object : RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                val totalItemCount = recyclerView!!.layoutManager.itemCount
//                if (totalItemCount == lastVisibleItemPosition + 1) {
//                    Log.d("MyTAG", "Load new list")
//                    recycler_view.removeOnScrollListener(scrollListener)
//                }
//            }
//        }
//        recycler_view.addOnScrollListener(scrollListener)
//    }

    fun searchMore(){
        page += 10
        executeSearch(et_searchQuery.text.toString())
    }

    fun executeSearch(searchQuery: String) {

        var searchOption = "sim";


        //todo SearchMore일때는 attach 형식? 아니면 clean? 아니면 내릴때마다 계속 알아서 검색(인스타 방식)
        ar_blogList.clear();

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