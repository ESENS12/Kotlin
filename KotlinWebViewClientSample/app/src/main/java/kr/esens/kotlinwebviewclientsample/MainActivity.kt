package kr.esens.kotlinwebviewclientsample

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.view.KeyEvent
import android.webkit.*
import android.widget.Toast
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    lateinit var mContext : Context
    private val STR_URL = "https://betafatosmap-web.fatos.biz/signup"
    companion object {
        lateinit var mWebView : WebView
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mWebView = findViewById(R.id.myWebView);
        mContext = this
        requestPermission()

        var myChromeClient = MyChromeClient()
        myChromeClient.mContext = mContext;

        mWebView.apply {
            webViewClient = myWebViewClient()
            webChromeClient = myChromeClient
            settings.javaScriptEnabled = true
//            settings.userAgentString = "Mozilla/5.0 (Linux; Android 4.1.1; MOGOS Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19"
            settings.setSupportMultipleWindows(true)
        }

    }

    private fun loadWebView(){

        if(mWebView != null){
            mWebView.loadUrl(STR_URL)
        }
    }

    inner class myWebViewClient : WebViewClient(){
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            return super.shouldOverrideUrlLoading(view, url)
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
        }

        override fun onLoadResource(view: WebView?, url: String?) {
            super.onLoadResource(view, url)
        }

        override fun onPageCommitVisible(view: WebView?, url: String?) {
            super.onPageCommitVisible(view, url)
        }

        override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
            return super.shouldInterceptRequest(view, url)
        }

        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            return super.shouldInterceptRequest(view, request)
        }

        override fun onTooManyRedirects(
            view: WebView?,
            cancelMsg: Message?,
            continueMsg: Message?
        ) {
            super.onTooManyRedirects(view, cancelMsg, continueMsg)
        }

        override fun onReceivedError(
            view: WebView?,
            errorCode: Int,
            description: String?,
            failingUrl: String?
        ) {
            super.onReceivedError(view, errorCode, description, failingUrl)
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
        }

        override fun onReceivedHttpError(
            view: WebView?,
            request: WebResourceRequest?,
            errorResponse: WebResourceResponse?
        ) {
            super.onReceivedHttpError(view, request, errorResponse)
        }

        override fun onFormResubmission(view: WebView?, dontResend: Message?, resend: Message?) {
            super.onFormResubmission(view, dontResend, resend)
        }

        override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
            super.doUpdateVisitedHistory(view, url, isReload)
        }

        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            super.onReceivedSslError(view, handler, error)
        }

        override fun onReceivedClientCertRequest(view: WebView?, request: ClientCertRequest?) {
            super.onReceivedClientCertRequest(view, request)
        }

        override fun onReceivedHttpAuthRequest(
            view: WebView?,
            handler: HttpAuthHandler?,
            host: String?,
            realm: String?
        ) {
            super.onReceivedHttpAuthRequest(view, handler, host, realm)
        }

        override fun shouldOverrideKeyEvent(view: WebView?, event: KeyEvent?): Boolean {
            return super.shouldOverrideKeyEvent(view, event)
        }

        override fun onUnhandledKeyEvent(view: WebView?, event: KeyEvent?) {
            super.onUnhandledKeyEvent(view, event)
        }

        override fun onScaleChanged(view: WebView?, oldScale: Float, newScale: Float) {
            super.onScaleChanged(view, oldScale, newScale)
        }

        override fun onReceivedLoginRequest(
            view: WebView?,
            realm: String?,
            account: String?,
            args: String?
        ) {
            super.onReceivedLoginRequest(view, realm, account, args)
        }

        override fun onRenderProcessGone(
            view: WebView?,
            detail: RenderProcessGoneDetail?
        ): Boolean {
            return super.onRenderProcessGone(view, detail)
        }

        override fun onSafeBrowsingHit(
            view: WebView?,
            request: WebResourceRequest?,
            threatType: Int,
            callback: SafeBrowsingResponse?
        ) {
            super.onSafeBrowsingHit(view, request, threatType, callback)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var bAllGranted = true
        if (requestCode == 1000) {
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this,
                        "require permissions denied",
                        Toast.LENGTH_SHORT
                    ).show()
                    finishAffinity()
                    bAllGranted = false
                    break
                }
            }

            if (bAllGranted) {
                try {
                    loadWebView()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pInternet: Int = checkSelfPermission(Manifest.permission.INTERNET)

            if (pInternet == PackageManager.PERMISSION_DENIED) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.INTERNET
                    ), 1000
                )
            } else {
                loadWebView()
            }
        } else {

            try {
                loadWebView()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}