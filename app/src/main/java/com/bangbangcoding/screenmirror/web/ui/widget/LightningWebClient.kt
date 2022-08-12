package com.bangbangcoding.screenmirror.web.ui.widget

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.MailTo
import android.util.Log
import android.view.LayoutInflater
import android.webkit.*
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.anthonycr.mezzanine.MezzanineGenerator
import com.bangbangcoding.screenmirror.BuildConfig
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.web.allowlist.AllowListModel
import com.bangbangcoding.screenmirror.web.controller.UIController
import com.bangbangcoding.screenmirror.web.di.injector
import com.bangbangcoding.screenmirror.web.logger.Logger
import com.bangbangcoding.screenmirror.web.ssl.SSLState
import com.bangbangcoding.screenmirror.web.ssl.SslWarningPreferences
import com.bangbangcoding.screenmirror.web.ui.model.apis_models.VideoInfo
import com.bangbangcoding.screenmirror.web.ui.preference.UserPreferences
import com.bangbangcoding.screenmirror.web.utils.*
import com.bangbangcoding.screenmirror.web.utils.Constants.FILE
import com.bangbangcoding.screenmirror.web.utils.ext.resizeAndShow
import com.bangbangcoding.screenmirror.web.utils.ext.snackbar
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.greenrobot.eventbus.EventBus
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.io.File
import java.net.URISyntaxException
import java.net.URL
import javax.inject.Inject


class LightningWebClient(
    private val activity: Activity,
    private val lightningView: LightningView
) : WebViewClient() {

    private var hasVideo : Boolean = false
    private val mapUrl : HashMap<String, Boolean> = HashMap()
    private val uiController: UIController
    private val intentUtils = IntentUtils(activity)
    private val emptyResponseByteArray: ByteArray = byteArrayOf()

//    @Inject
//    internal lateinit var proxyUtils: ProxyUtils

    @Inject
    internal lateinit var userPreferences: UserPreferences

    @Inject
    internal lateinit var sslWarningPreferences: SslWarningPreferences

    @Inject
    internal lateinit var whitelistModel: AllowListModel

    @Inject
    internal lateinit var logger: Logger

    @NonNull
    internal var subscription = CompositeSubscription()

//    private var adBlock: AdBlocker

    @Volatile
    private var isRunning = false
    private var zoomScale = 0.0f


    private val textReflowJs = MezzanineGenerator.TextReflow()
    private val invertPageJs = MezzanineGenerator.InvertPage()

    private var cookies: String = ""

    private var currentUrl: String = ""
    private var urlLoadResource: String = ""

    private var xxxUrl: String = ""
    private var isPost = false
    var sslState: SSLState = SSLState.None
        private set(value) {
            sslStateSubject.onNext(value)
            field = value
        }

    private val sslStateSubject: PublishSubject<SSLState> = PublishSubject.create()

    init {
        uiController = activity as UIController
        activity.injector.inject(this)
        //        adBlock = chooseAdBlocker()
    }


    fun sslStateObservable(): Observable<SSLState> = sslStateSubject.hide()

    fun updatePreferences() {
//        adBlock = chooseAdBlocker()
    }

    private fun isAd(pageUrl: String, requestUrl: String) = true
    /*!whitelistModel.isUrlAllowedAds(pageUrl)*/ /*&& adBlock.isAd(requestUrl)*/

    override fun onPageFinished(view: WebView, url: String) {
        if (url.startsWith("intent://threads/") || url.startsWith("market://details?id=")) {
            currentUrl = url
            if (view.isShown) {
                uiController.updateUrl(url, false)
                uiController.setBackButtonEnabled(view.canGoBack())
                uiController.setForwardButtonEnabled(view.canGoForward())
                view.postInvalidate()
            }
            if (view.title == null || view.title!!.isEmpty()) {
                lightningView.titleInfo.setTitle(activity.getString(R.string.untitled))
            } else {
                lightningView.titleInfo.setTitle(view.title)
            }
            if (lightningView.invertPage) {
                view.evaluateJavascript(invertPageJs.provideJs(), null)
            }
            uiController.tabChanged(lightningView)
        }

        val cookies = CookieManager.getInstance()
        cookies?.getCookie(url)
        Log.d("ttt", "All the cookies in a string:${cookies?.getCookie(url)}")
    }

    override fun onLoadResource(view: WebView?, url: String?) {
        val xUrl = view?.url
        super.onLoadResource(view, url)
//        val hitTestResult = view?.hitTestResult
//        if(hitTestResult!= null && hitTestResult.type > 0){
//            Log.e("ttt", "hit")
//        }
       //https://scontent.cdninstagram.com/v/t50.16885-16/280452900_1129773097870169_7696465520824993348_n.mp4?_nc_ht=instagram.fhan5-6.fna.fbcdn.net&_nc_cat=105&_nc_ohc=f57FgMlsq7QAX_xUxSS&edm=AJ9x6zYBAAAA&ccb=7-4&oh=00_AT_QWOr8wFpDfp7ya42R45_DRiXbxtf0hlZuzg60miqKDQ&oe=627AC8F3&_nc_sid=cff2a4&
       //https://scontent.cdninstagram.com/v/t50.16885-16/280189484_723112458831144_5993933691775085998_n.mp4?_nc_ht=instagram.fhan5-7.fna.fbcdn.net&_nc_cat=100&_nc_ohc=wG4eyA6YVcIAX8Bg4mS&edm=AIQHJ4wBAAAA&ccb=7-4&oh=00_AT9D1THvPaASJ_wS0lmeZcnx_AKXmd5pzNR50wYAdkzFHA&oe=627AD1DA&_nc_sid=7b02f1&
       //https://scontent.cdninstagram.com/v/t50.16885-16/280247150_113435704571164_8367473605568682045_n.mp4?_nc_ht=instagram.fhan5-2.fna.fbcdn.net&_nc_cat=104&_nc_ohc=5MOJEVWSdkQAX-zIt89&edm=AJ9x6zYBAAAA&ccb=7-4&oh=00_AT9ddKh5hbIBVRMO1JKhcaaSVLzTdmR8mf6CaqY8t5o8aw&oe=627AB002&_nc_sid=cff2a4&
       //https://scontent.cdninstagram.com/v/t50.16885-16/279826255_686294549262829_4095996488462758895_n.mp4?_nc_ht=instagram.fhan5-10.fna.fbcdn.net&_nc_cat=101&_nc_ohc=3b2YlWirkZAAX88jazY&edm=AJ9x6zYBAAAA&ccb=7-4&oh=00_AT9BniWtsg3TsyIlV_rBj3GiFpzcs8gVc_EvDLg7Y7QWvg&oe=627B26C5&_nc_sid=cff2a4&
       //https://scontent.cdninstagram.com/v/t50.16885-16/280410590_567330251375540_904396821746728782_n.mp4?_nc_ht=instagram.fhan5-8.fna.fbcdn.net&_nc_cat=108&_nc_ohc=-cxjbenhy6kAX_El2Zb&edm=AJ9x6zYBAAAA&ccb=7-4&oh=00_AT-Cl4yYfB5ry_ckdJGRo1kb83NPrvo03LfvqoTlc2r4bg&oe=627ABAD0&_nc_sid=cff2a4&

        if (url!!.startsWith("intent://threads/") || url.startsWith("market://details?id=")) {
            view?.loadUrl("https://m.facebook.com/messenger/install/?native_url=intent%3A%2F%2Fthreads%2F%23Intent%3Bscheme%3Dfb-messenger%3Bpackage%3Dcom.facebook.orca%3Bend&_rdr")
        } else if (!currentUrl.contains("youtu") && xUrl != null) {
            var urlVideo: String = url
            if (lightningView.isShown && url.contains("mp4")) {
                Log.e(
                    "ttt",
                    "onLoadResource:\n| current: $currentUrl \n| url: $url \n| view?.url: $xUrl \n| xxxUrl: $xxxUrl"
                )

                if (currentUrl.contains("facebook.com") && !currentUrl.contains("facebook.com/login") && !currentUrl.contains(
                        "facebook.com/?_rdr"
                    ) && !isPost
                ) {
                    Log.e("ttt", "onLoadResource: $currentUrl")
                    EventBus.getDefault().postSticky("showDialog")
                    isPost = true
                }
                if (currentUrl.contains("youtube")) {
                    EventBus.getDefault().postSticky("youtube")
                }

                if (url.contains("mp4") && url.contains("tiktok")
                    && !(url.contains(".ts?") || url.contains("seg-"))
                ) {
                    val videoInfo = VideoInfo().apply {
                        this.url = url
                        this.title = "Tiktok_${System.currentTimeMillis()}"
                        format = "480P"
                        ext = ".mp4"
                    }
                    subscription.add(getRemoteFileSize(url).filter { integer -> integer > 0 }
                        .doOnNext { size -> videoInfo.fileSize = size.toLong() }
                        .subscribe(object : Subscriber<Int>() {
                            override fun onCompleted() {}
                            override fun onError(e: Throwable) {}

                            override fun onNext(size: Int?) {
                                Log.e("ttt", "onNext: $url")
                                if (!currentUrl.contains("facebook.com")) {
                                    val urlListDownload = ArrayList<VideoInfo>()
                                    urlListDownload.add(videoInfo)
                                    lightningView.detectListener?.onDataResult(true, true, urlListDownload)
                                }
                            }
                        }))
                } else if (urlVideo.contains("babeporn") || urlVideo.contains("porndotcom") || urlVideo.contains(
                        "fullporn"
                    )
                    || urlVideo.contains("fullxxxvideo") || urlVideo.contains("lucah")
                    || urlVideo.contains("xnxxarab") || urlVideo.contains("ixxx")
                    || urlVideo.contains("sexgai") || urlVideo.contains("lontv")
                ) {
                    view.evaluateJavascript(
                        ScriptUtil.BABE_P
                    ) { p0 ->
                        if (!p0.contains("_000_")) {
                            val link = p0
                            Log.e("ttt", "babeporn link: $link")
                            val urlListDownload = ArrayList<VideoInfo>()
                            val videoInfo = VideoInfo().apply {
                                this.url = link
                                this.title = "${System.currentTimeMillis()}"
                                format = "480P"
                                ext = ".mp4"
                                fileSize = 0L
                            }
                            urlListDownload.add(videoInfo)
                            Log.e("ttt", "babeporn size: ${urlListDownload.size}")
                            lightningView.detectListener?.onDataResult(true, true, urlListDownload)
                        }
                    }
                } else if (urlVideo.contains("xhamster.com") && url.contains(".mp4")) {
                    view.evaluateJavascript(
                        ScriptUtil.HAMSTER
                    ) { p0 ->
                        if (!p0.contains("_000_")) {
                            val link = p0
                            Log.e("ttt", "xhamster link: $link")
                            val urlListDownload = ArrayList<VideoInfo>()
                            val videoInfo = VideoInfo().apply {
                                this.url = link
                                this.title = "${System.currentTimeMillis()}"
                                format = "480P"
                                ext = ".mp4"
                                fileSize = 0L
                            }
                            urlListDownload.add(videoInfo)
                            Log.e("ttt", "xhamster size: ${urlListDownload.size}")
                            lightningView.detectListener?.onDataResult(true, true, urlListDownload)
                        }
                    }
                } else if (urlVideo.contains("lontv")) {
                    view.evaluateJavascript(
                        ScriptUtil.BABYLON
                    ) { p0 ->
                        if (!p0.contains("_000_")) {
                            val link = p0
                            Log.e("ttt", "Babylon link: $link")
                            val urlListDownload = ArrayList<VideoInfo>()
                            val videoInfo = VideoInfo().apply {
                                this.url = link
                                this.title = "${System.currentTimeMillis()}"
                                format = "480P"
                                ext = ".mp4"
                                fileSize = 0L
                            }
                            urlListDownload.add(videoInfo)
                            Log.e("ttt", "babylon size: ${urlListDownload.size}")
                            lightningView.detectListener?.onDataResult(true, true, urlListDownload)
                        }
                    }
                } else if (urlVideo.contains("ijavhd.com")) {
                    view.evaluateJavascript(
                        ScriptUtil.IJJJ
                    ) { p0 ->
                        if (!p0.contains("_000_")) {
                            val link = p0
                            Log.e("ttt", "Japan link: $link")
                            val urlListDownload = ArrayList<VideoInfo>()
                            val videoInfo = VideoInfo().apply {
                                this.url = link
                                this.title = "Japan_${System.currentTimeMillis()}"
                                format = "480P"
                                ext = ".mp4"
                                fileSize = 0L
                            }
                            urlListDownload.add(videoInfo)
                            Log.e("ttt", "Japan size: ${urlListDownload.size}")
                            lightningView.detectListener?.onDataResult(true, true, urlListDownload)
                        }
                    }
                } else if (urlVideo.contains("xvideos") || urlVideo.contains("xnxx.com") || urlVideo.contains(
                        "xvideos2"
                    )
                ) {
                    view.loadUrl(ScriptUtil.XXX1)
                } else if (xUrl.contains("pornhub.com")) {
                    Log.e("ttt", "onLoadResource: load nè")
                    view.loadUrl(ScriptUtil.PORX)
                } else if (xUrl.contains("imdb.com")) {
                    Log.e("ttt", "imdb: load nè")
                    if (url.contains(".mp4")) {
                        val urlListDownload = ArrayList<VideoInfo>()
                        val videoInfo = VideoInfo().apply {
                            this.url = url
                            this.title = "IMDB_${System.currentTimeMillis()}"
                            format = "480P"
                            ext = ".mp4"
                            fileSize = 0L
                        }
                        urlListDownload.add(videoInfo)
                        lightningView.detectListener!!.onDataResult(true, true, urlListDownload)
                        return
                    }
                } else if (xUrl.contains("linkedin.com") && url.contains("mp4")
                    && !xUrl.contains("utm_source") && !xUrl.contains("/posts/")) {
                    Log.e("ttt", "linkedin: load nè")
//                    if (url.contains("efg")) {
                        val urlListDownload = ArrayList<VideoInfo>()
                        val videoInfo = VideoInfo().apply {
                            this.url = url
                            this.title = "WEB_LINK_${System.currentTimeMillis()}"
                            format = "480P"
                            ext = ".mp4"
                            fileSize = 0L
                        }
                        urlListDownload.add(videoInfo)
                        lightningView.detectListener!!.onDataResult(true, true, urlListDownload)
                        return
//                    } else {
//                        activity.snackbar(R.string.error_reset_page)
//                    }
                } /*else if (xUrl.contains("dailymotion.com")) {
                    if (*//*url.contains(".mp4") || *//*url.contains(".ts")) {
                        if (xUrl == currentUrl ) {
                            if (hasVideo) {
                                return
                            }
                        } else {
                            hasVideo = false
                        }
                        hasVideo = true
                        Log.e("ttt", "chekc mp4 nè")
                        val urlListDownload = ArrayList<VideoInfo>()
                        val urlR = url.replace("(frag\\()+(\\d+)+(\\))".toRegex(), "FRAGMENT")
                        val videoInfo = VideoInfo().apply {
                            this.url = urlR
                            this.title = "WEB_${System.currentTimeMillis()}"
                            format = "480P"
                            ext = ".ts"
                            fileSize = 0L
                        }
                        urlListDownload.add(videoInfo)
                        lightningView.detectListener!!.onDataResult(true, true, urlListDownload)
                        return
                    }
                }*/

                else if (/*xUrl == currentUrl &&*/
                    xUrl.contains("linked.com")
                ) {
                    if (url.contains(".mp4")) {
                        val urlListDownload = ArrayList<VideoInfo>()
                        val videoInfo = VideoInfo().apply {
                            this.url = url
                            this.title = "WEB_${System.currentTimeMillis()}"
                            format = "720P"
                            ext = ".mp4"
                            fileSize = 0L

                        }
                        urlListDownload.add(videoInfo)
                        lightningView.detectListener!!.onDataResult(true, true, urlListDownload)
                        return
                    }
                } else if (xUrl.contains("facebook.com") || xUrl.contains("fb.watch")) {
                    //Detect fb web
                    checkWebFB(view, url)

                }/*else if (xxxUrl != xUrl && (xUrl.contains("dailymotion") || xUrl.contains("dai.ly"))){
                    if(xUrl.contains("video")){
                        Log.e("ttt", "dailymotion : detect webView.url= $xUrl")
                        EventBus.getDefault().postSticky("rotate")
                        DetectVideosMain.startDetect(
                            activity,
                            xUrl,
                            false,
                            lightningView.detectListener
                        )
                        xxxUrl = xUrl
                    }
                }*/ else if (xUrl.contains("instagram") && !(url.contains(".ts?") || url.contains("seg-")))
                {
                    Log.e("ttt", "checkInstagram = " + lightningView.checkInstagram)
                    if (lightningView.checkInstagram &&url.contains("mp4") && !(xUrl.contains("/reel/")||xUrl.contains("/tv/") ||xUrl.contains("/p/")))
                    {
                        //checkWebInstagram(url)
                            //updating...
                        if (url.contains("bytestart")){
                            var newUrl = convertUrlInsta(url)
                            checkWebInstagram(newUrl)
                        }else {
                            checkWebInstagram(url)
                        }
                        lightningView.checkInstagram = false
                    }else if (xxxUrl != xUrl && url.contains("mp4")){
                        Log.e("ttt", "onLoadResource: detect webView.url= $xUrl")
                        EventBus.getDefault().postSticky("rotate")
//                        DetectVideosMain.startDetect(
//                            activity,
//                            xUrl,
//                            false,
//                            lightningView.detectListener
//                        )
                        xxxUrl = xUrl
                    }
                }else if (xUrl.contains("twitter.com")){ //updating...
                    Log.e("ttt", "onLoadResource: detect webView.url= $url")
                    EventBus.getDefault().postSticky("rotate")
//                    DetectVideosMain.startDetect(
//                        activity,
//                        url,
//                        false,
//                        lightningView.detectListener
//                    )
                    // xxxUrl = xUrl
                }
                else if (xxxUrl != xUrl) {
                    Log.e("ttt", "onLoadResource: detect webView.url= $xUrl")
                    EventBus.getDefault().postSticky("rotate")
                    xxxUrl = xUrl
//                    DetectVideosMain.startDetect(
//                        activity,
//                        xUrl,
//                        false,
//                        lightningView.detectListener
//                    )
                }
            }
        }
    }

    private fun convertUrlInsta(url : String):String{
        val listStr: List<String> = url.split("bytestart")
        return listStr.get(0)
    }

    private fun checkWebInstagram(url: String){
        val urlListDownload = ArrayList<VideoInfo>()
        val videoInfo = VideoInfo().apply {
            this.url = url
            this.title = "WEB_INS_${System.currentTimeMillis()}"
            format = "720P"
            ext = ".mp4"
            Log.e("ttt", "newUrl := $url")
        }
        urlListDownload.add(videoInfo)
        lightningView.detectListener!!.onDataResult(true, true, urlListDownload)
        return


//        subscription.add(getRemoteFileSize(url).filter { integer -> integer > 0 }
//            .doOnNext { size -> videoInfo.fileSize = size.toLong() }
//            .subscribe(object : Subscriber<Int>() {
//                override fun onCompleted() {}
//                override fun onError(e: Throwable) {}
//
//                override fun onNext(size: Int?) {
//                    Log.e("ttt", "onNext99: $url")
//                    if (!currentUrl.contains("facebook.com")) {
//                        val urlListDownload = ArrayList<VideoInfo>()
//                        urlListDownload.add(videoInfo)
//                        lightningView.detectListener?.onDataResult(true, true, urlListDownload)
//                    }
//                }
//            }))
    }

    private fun checkWebFB(view: WebView, url : String) {
        Log.e("ttt", "onLoadResource FB Script nè")
        view.evaluateJavascript(
            ScriptUtil.FB_V2
        ) { p0 ->
            if (!p0.contains("_000_")/* && !p0.equals(idFB)*/) {
//                            idFB = p0
                val newID = p0.replace("\"", "")
                val currentPage = "https://www.facebook.com/watch/?v=$newID"
                Log.e("ttt", "onReceiveValue: FB_V2 $newID")
                EventBus.getDefault().postSticky("rotate")
//                DetectVideosMain.startDetect(
//                    activity,
//                    currentPage,
//                    false,
//                    lightningView.detectListener
//                )
            } else {
                getFb3(view, url)
            }
        }
    }

    private fun getFb3(view: WebView, url : String) {
        view.evaluateJavascript(
            ScriptUtil.FB_V3
        ) { p0 ->
            if (!p0.contains("_696_")) {
                var link = p0
                Log.e("ttt", "FB_V3: $link")
                link = link.replace("\"", "")
                val index = link.indexOf("https://www.")
                var l = link
                if (index >= 0) {
                    l = link.substring(index).trim()
                }
                Log.e("ttt", "FB_V3 substring: $link || $index || $l")
//                DetectVideosMain.startDetect(
//                    activity,
//                    l,
//                    false,
//                    lightningView.detectListener
//                )
            } else {
                if (url.contains(".mp4")) {
                    val urlListDownload = ArrayList<VideoInfo>()
                    val videoInfo = VideoInfo().apply {
                        this.url = url
                        this.title = "WEB_${System.currentTimeMillis()}"
                        format = "480P"
                        ext = ".mp4"
                        fileSize = 0L
                    }
                    urlListDownload.add(videoInfo)
                    lightningView.detectListener!!.onDataResult(true, true, urlListDownload)
                }
            }
        }
    }

    private fun getRemoteFileSize(remoteFileUrl: String): rx.Observable<Int> {

        return rx.Observable.create(rx.Observable.OnSubscribe<Int> { subscriber ->
            try {
                val urlConnection = URL(remoteFileUrl).openConnection()
                urlConnection.connect()
                subscriber!!.onNext(Integer.valueOf(urlConnection.contentLength))
                subscriber.onCompleted()
            } catch (e: Exception) {
                subscriber!!.onError(e)
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
        setUserAgentRuntime(view)
        EventBus.getDefault().post(url)
        if (currentUrl != url) {
            EventBus.getDefault().post("refreshButton")
        }

        Log.e("ttt", "onPageStarted: --> $url")

//        sslState = if (URLUtil.isHttpsUrl(url)) {
//            SSLState.Valid
//        } else {
//            SSLState.None
//        }
        lightningView.titleInfo.setFavicon(null)
        lightningView.titleInfo.setScreenShot(null)
        if (lightningView.isShown)  {
            currentUrl = url
            if (url.contains(".html")) {
                view.settings.userAgentString = ""
            } else {
//                https://www.instagram.com/tv/CcGINQDjKsq/?utm_source=ig_web_copy_link
                Log.e("ttt", "onPageStarted: call detect --> $url")
//                DetectVideosMain.startDetect(
//                    activity,
//                    url,
//                    false,
//                    lightningView.detectListener)
            }
            uiController.updateUrl(url, true)
            uiController.showActionBar()
        }
        uiController.tabChanged(lightningView)
    }

    private fun setUserAgentRuntime(webView: WebView) {
        val settings = webView.settings

        webView.url?.let {
            if (it.contains("facebook.com")
                || it.contains("twitter.com")
                || it.contains("linkedin.com")
                || it.contains("dailymotion.com/signin")
                || it.contains("youtube.com")
                || it.contains("tiktok.com")
                || it.contains("accounts.google.com")
            ) {
                val agent = System.getProperty("http.agent")
                Log.e(TAG, "initializePreferences: $agent")
                settings.userAgentString = agent
            } else {
                settings.userAgentString = ""
            }
        }
    }

    override fun onReceivedHttpAuthRequest(
        view: WebView, handler: HttpAuthHandler,
        host: String, realm: String
    ) =
        AlertDialog.Builder(activity).apply {
            val dialogView =
                LayoutInflater.from(activity).inflate(R.layout.dialog_auth_request, null)

            val realmLabel = dialogView.findViewById<TextView>(R.id.auth_request_realm_textview)
            val name = dialogView.findViewById<EditText>(R.id.auth_request_username_edittext)
            val password = dialogView.findViewById<EditText>(R.id.auth_request_password_edittext)

            realmLabel.text = activity.getString(R.string.label_realm)

            setView(dialogView)
            setTitle(R.string.title_sign_in)
            setCancelable(true)
            setPositiveButton(R.string.title_sign_in) { _, _ ->
                val user = name.text.toString()
                val pass = password.text.toString()
                handler.proceed(user.trim(), pass.trim())
                logger.log(TAG, "Attempting HTTP Authentication")
            }
            setNegativeButton(R.string.txt_cancel) { _, _ ->
                handler.cancel()
            }
        }.resizeAndShow()

    override fun onScaleChanged(view: WebView, oldScale: Float, newScale: Float) {
        if (view.isShown && lightningView.userPreferences.textReflowEnabled) {
            if (isRunning)
                return
            val changeInPercent = Math.abs(100 - 100 / zoomScale * newScale)
            if (changeInPercent > 2.5f && !isRunning) {
                isRunning = view.postDelayed({
                    zoomScale = newScale
                    view.evaluateJavascript(textReflowJs.provideJs()) { isRunning = false }
                }, 100)
            }

        }
    }

    private fun continueLoadingUrl(webView: WebView, url: String, headers: Map<String, String>) =
        when {
            headers.isEmpty() -> false
            ApiUtils.doesSupportWebViewHeaders() -> {
                webView.loadUrl(url, headers)
                true
            }
            else -> false
        }

    private fun isMailOrIntent(url: String, view: WebView): Boolean {
        if (url.startsWith("mailto:")) {
            val mailTo = MailTo.parse(url)
            val i = Utils.newEmailIntent(
                mailTo.to, mailTo.subject,
                mailTo.body, mailTo.cc
            )
            activity.startActivity(i)
            view.reload()
            return true
        } else if (url.startsWith("intent://")) {
            val intent = try {
                Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
            } catch (ignored: URISyntaxException) {
                null
            }

            if (intent != null) {
                intent.addCategory(Intent.CATEGORY_BROWSABLE)
                intent.component = null
                intent.selector = null
                try {
                    activity.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }

                return true
            }
        } else if (URLUtil.isFileUrl(url) && !UrlUtils.isSpecialUrl(url)) {
            val file = File(url.replace(FILE, ""))

            if (file.exists()) {
                val newMimeType = MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(Utils.guessFileExtension(file.toString()))

                val intent = Intent(Intent.ACTION_VIEW)
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                val contentUri = FileProvider.getUriForFile(
                    activity,
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    file
                )
                intent.setDataAndType(contentUri, newMimeType)

                try {
                    activity.startActivity(intent)
                } catch (e: Exception) {
                    println("LightningWebClient: cannot open downloaded file")
                }

            } else {
                activity.snackbar(R.string.message_open_download_fail)
            }
            return true
        }
        return false
    }

    companion object {

        private const val TAG = "LightningWebClient"

    }

}
