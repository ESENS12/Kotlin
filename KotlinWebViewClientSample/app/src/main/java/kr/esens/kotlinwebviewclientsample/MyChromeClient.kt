package kr.esens.kotlinwebviewclientsample

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Message
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.browser.customtabs.CustomTabsIntent

class MyChromeClient : WebChromeClient() {

    private val TAG = "MyChromeClient"
    lateinit var mContext : Context
    lateinit var urlString : String

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        Log.d(TAG,"onProgressChanged called : ${newProgress}")
        super.onProgressChanged(view, newProgress)
    }

    override fun onReceivedTitle(view: WebView?, title: String?) {
        Log.d(TAG,"onReceivedTitle called : ${title}")
        super.onReceivedTitle(view, title)
    }

    override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
        Log.d(TAG,"onReceivedIcon called : ${icon.toString()}")
        super.onReceivedIcon(view, icon)
    }

    override fun onReceivedTouchIconUrl(view: WebView?, url: String?, precomposed: Boolean) {
        Log.d(TAG,"onReceivedTouchIconUrl called : ${url}")
        super.onReceivedTouchIconUrl(view, url, precomposed)
    }

    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        Log.d(TAG,"onReceivedTouchIconUrl called")
        super.onShowCustomView(view, callback)
    }

    override fun onShowCustomView(
        view: View?,
        requestedOrientation: Int,
        callback: CustomViewCallback?
    ) {
        super.onShowCustomView(view, requestedOrientation, callback)
    }

    override fun onHideCustomView() {
        Log.d(TAG,"onHideCustomView called")
        super.onHideCustomView()
    }

    override fun onCreateWindow(
        view: WebView?,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message?
    ): Boolean {
        Log.d(TAG,"onCreateWindow called")
        val builder = CustomTabsIntent.Builder()
        builder.setShowTitle(false)
        builder.setShareState(CustomTabsIntent.SHARE_STATE_OFF)
        builder.setInstantAppsEnabled(true)
        val customBuilder  = builder.build();

        val request_uri = Uri.parse(urlString)
        customBuilder.launchUrl(mContext, request_uri)
//        val newWebView = WebView(MainActivity@mContext).apply {
//            settings.javaScriptEnabled = true
//        }
//
//        val dialog = Dialog(MainActivity@mContext).apply {
//            setContentView(newWebView)
//            window!!.attributes.width = ViewGroup.LayoutParams.WRAP_CONTENT
//            window!!.attributes.height = ViewGroup.LayoutParams.WRAP_CONTENT
//            show()
//        }
//        newWebView.webChromeClient = object : WebChromeClient(){
//            override fun onCloseWindow(window: WebView?) {
//                dialog.dismiss()
//            }
//        }
//        (resultMsg?.obsj as WebView.WebViewTransport).webView = newWebView
//        resultMsg.sendToTarget()
        return true
//        return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
    }

    override fun onRequestFocus(view: WebView?) {
        Log.d(TAG,"onRequestFocus called")
        super.onRequestFocus(view)
    }

    override fun onCloseWindow(window: WebView?) {
        Log.d(TAG,"onCloseWindow called")
        super.onCloseWindow(window)
    }

    override fun onJsAlert(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        Log.d(TAG,"onJsAlert called url : $url , msg : $message")
        return super.onJsAlert(view, url, message, result)
    }

    override fun onJsConfirm(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        Log.d(TAG,"onJsConfirm called url : $url , msg : $message")
        return super.onJsConfirm(view, url, message, result)
    }

    override fun onJsPrompt(
        view: WebView?,
        url: String?,
        message: String?,
        defaultValue: String?,
        result: JsPromptResult?
    ): Boolean {
        Log.d(TAG,"onJsConfirm called url : $url , msg : $message")
        return super.onJsPrompt(view, url, message, defaultValue, result)
    }

    override fun onJsBeforeUnload(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        Log.d(TAG,"onJsBeforeUnload called url : $url , msg : $message")
        return super.onJsBeforeUnload(view, url, message, result)
    }

    override fun onExceededDatabaseQuota(
        url: String?,
        databaseIdentifier: String?,
        quota: Long,
        estimatedDatabaseSize: Long,
        totalQuota: Long,
        quotaUpdater: WebStorage.QuotaUpdater?
    ) {
        super.onExceededDatabaseQuota(
            url,
            databaseIdentifier,
            quota,
            estimatedDatabaseSize,
            totalQuota,
            quotaUpdater
        )
    }

    override fun onReachedMaxAppCacheSize(
        requiredStorage: Long,
        quota: Long,
        quotaUpdater: WebStorage.QuotaUpdater?
    ) {
        super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater)
    }

    override fun onGeolocationPermissionsShowPrompt(
        origin: String?,
        callback: GeolocationPermissions.Callback?
    ) {
        super.onGeolocationPermissionsShowPrompt(origin, callback)
    }

    override fun onGeolocationPermissionsHidePrompt() {
        super.onGeolocationPermissionsHidePrompt()
    }

    override fun onPermissionRequest(request: PermissionRequest?) {
        Log.d(TAG,"onPermissionRequest called request : ${request.toString()}")

        super.onPermissionRequest(request)
    }

    override fun onPermissionRequestCanceled(request: PermissionRequest?) {
        super.onPermissionRequestCanceled(request)
    }

    override fun onJsTimeout(): Boolean {
        return super.onJsTimeout()
    }

    override fun onConsoleMessage(message: String?, lineNumber: Int, sourceID: String?) {
        super.onConsoleMessage(message, lineNumber, sourceID)
    }

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {

        Log.e(TAG,"onConsoleMessage called msg : ${consoleMessage?.message()}")
        return super.onConsoleMessage(consoleMessage)
    }

    override fun getDefaultVideoPoster(): Bitmap? {
        return super.getDefaultVideoPoster()
    }

    override fun getVideoLoadingProgressView(): View? {
        return super.getVideoLoadingProgressView()
    }

    override fun getVisitedHistory(callback: ValueCallback<Array<String>>?) {
        super.getVisitedHistory(callback)
    }

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
    }
}