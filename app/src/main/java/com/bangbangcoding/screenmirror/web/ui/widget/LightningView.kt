package com.bangbangcoding.screenmirror.web.ui.widget

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.View.OnTouchListener
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebSettings.LayoutAlgorithm
import android.webkit.WebView
import androidx.collection.ArrayMap
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.bangbangcoding.screenmirror.web.controller.UIController
import com.bangbangcoding.screenmirror.web.di.injector
import com.bangbangcoding.screenmirror.web.di.module.DatabaseScheduler
import com.bangbangcoding.screenmirror.web.di.module.MainScheduler
import com.bangbangcoding.screenmirror.web.logger.Logger
import com.bangbangcoding.screenmirror.web.ssl.SSLState
import com.bangbangcoding.screenmirror.web.ui.dialog.LightningDialogBuilder
import com.bangbangcoding.screenmirror.web.ui.model.apis_models.VideoInfo
import com.bangbangcoding.screenmirror.web.ui.paste.DownloadVideosMain
import com.bangbangcoding.screenmirror.web.ui.preference.UserPreferences
import com.bangbangcoding.screenmirror.web.utils.Constants.DESKTOP_USER_AGENT
import com.bangbangcoding.screenmirror.web.utils.Constants.MOBILE_USER_AGENT
import com.bangbangcoding.screenmirror.web.utils.NetworkConnectivityModel
import com.bangbangcoding.screenmirror.web.utils.UrlUtils
import com.bangbangcoding.screenmirror.web.utils.Utils
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import org.greenrobot.eventbus.EventBus
import org.json.JSONArray
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * [LightningView] acts as a tab for the browser, handling WebView creation and handling logic, as
 * well as properly initialing it. All interactions with the WebView should be made through this
 * class.
 */

class LightningView(
    private val activity: Activity,
    tabInitializer: TabInitializer,
    val isIncognito: Boolean,
    private val homePageInitializer: HomePageInitializer,
    private val logger: Logger,
    val userPreferences: UserPreferences
) {

    /**
     * Getter for the [LightningViewTitle] of the current LightningView instance.
     *
     * @return a NonNull instance of LightningViewTitle
     */
    val titleInfo: LightningViewTitle

    var detectListener: DownloadVideosMain.OnDetectListener? = null

    /**
     * Gets the current WebView instance of the tab.
     *
     * @return the WebView instance of the tab, which can be null.
     */
    var webView: WebView? = null
        private set

    private val uiController: UIController
    private val gestureDetector: GestureDetector
    private val paint = Paint()

    /**
     * Sets whether this tab was the result of a new intent sent to the browser.
     */
    var isNewTab: Boolean = false

    var focus:Boolean = false

    var checkInstagram = false

    /**
     * This method sets the tab as the foreground tab or the background tab.
     */
    var isForegroundTab: Boolean = false
        set(isForeground) {
            field = isForeground
            uiController.tabChanged(this)
        }

    /**
     * Gets whether or not the page rendering is inverted or not. The main purpose of this is to
     * indicate that JavaScript should be run at the end of a page load to invert only the images
     * back to their non-inverted states.
     *
     * @return true if the page is in inverted mode, false otherwise.
     */
    var invertPage = false
        private set
    private var toggleDesktop = false
    private val webViewHandler = WebViewHandler(this)

    /**
     * This method gets the additional headers that should be added with each request the browser
     * makes.
     *
     * @return a non null Map of Strings with the additional request headers.
     */
    internal val requestHeaders = ArrayMap<String, String>()

    private var maxFling: Float

    //    @Inject
//    internal lateinit var userPreferences: UserPreferences
    @Inject
    internal lateinit var dialogBuilder: LightningDialogBuilder

//    @Inject
//    internal lateinit var proxyUtils: ProxyUtils

    @Inject
    @DatabaseScheduler
    internal lateinit var databaseScheduler: Scheduler

    @Inject
    lateinit var networkConnectivityModel: NetworkConnectivityModel

    @Inject
    @MainScheduler
    internal lateinit var mainScheduler: Scheduler

    private var lightningWebClient: LightningWebClient

//    private val networkDisposable: Disposable

    /**
     * This method determines whether the current tab is visible or not.
     *
     * @return true if the WebView is non-null and visible, false otherwise.
     */
    val isShown: Boolean
        get() = webView?.isShown == true

    /**
     * Gets the current progress of the WebView.
     *
     * @return returns a number between 0 and 100 with the current progress of the WebView. If the
     * WebView is null, then the progress returned will be 100.
     */
    val progress: Int
        get() = webView?.progress ?: 100

    /**
     * Get the current user agent used by the WebView.
     *
     * @return retuns the current user agent of the WebView instance, or an empty string if the
     * WebView is null.
     */
    private val userAgent: String
        get() = webView?.settings?.userAgentString ?: ""

    /**
     * Gets the favicon currently in use by the page. If the current page does not have a favicon,
     * it returns a default icon.
     *
     * @return a non-null Bitmap with the current favicon.
     */
    val favicon: Bitmap
        get() = titleInfo.getFavicon(uiController.getUseDarkTheme())

    val screenShot: Bitmap
        get() = titleInfo.getScreenShot(url)

    /**
     * Get the current title of the page, retrieved from the title object.
     *
     * @return the title of the page, or an empty string if there is no title.
     */
    val title: String
        get() = titleInfo.getTitle() ?: ""

    /**
     * Get the current URL of the WebView, or an empty string if the WebView is null or the URL is
     * null.
     *
     * @return the current URL or an empty string.
     */
    val url: String
        get() = webView?.url ?: ""

    init {
        activity.injector.inject(this)
        uiController = activity as UIController

        titleInfo = LightningViewTitle(activity)

        maxFling = ViewConfiguration.get(activity).scaledMaximumFlingVelocity.toFloat()
        lightningWebClient = LightningWebClient(activity, this)
        gestureDetector = GestureDetector(activity, CustomGestureListener())

        try {
            webView = WebView(activity).apply {
                id = View.generateViewId()

                isFocusableInTouchMode = true
                isFocusable = true
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    isAnimationCacheEnabled = false
                    isAlwaysDrawnWithCacheEnabled = false
                }
                setBackgroundColor(Color.WHITE)

                isScrollbarFadingEnabled = true
                isSaveEnabled = true
                setNetworkAvailable(true)
                webChromeClient = LightningChromeClient(activity, this@LightningView)
                webViewClient = lightningWebClient

                setDownloadListener(LightningDownloadListener(activity))
                setOnTouchListener(TouchListener())
                initializeSettings()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        initializePreferences()

        tabInitializer.initialize(webView!!, requestHeaders)

//        networkDisposable = networkConnectivityModel.connectivity()
//            .observeOn(mainScheduler)
//            .subscribe(::setNetworkAvailable)

        webView?.addJavascriptInterface(this, "AndroidDownloader")
    }

    fun isFocus(): Boolean {
        return webView?.url?.contains("homepage") ?: false
    }

    fun currentSslState(): SSLState = lightningWebClient.sslState

    fun sslStateObservable(): Observable<SSLState> = lightningWebClient.sslStateObservable()

    /**
     * This method loads the homepage for the browser. Either it loads the URL stored as the
     * homepage, or loads the startpage or bookmark page if either of those are set as the homepage.
     */
    fun loadHomePage() {
        reinitialize(homePageInitializer)
    }

    private fun reinitialize(tabInitializer: TabInitializer) {
        webView?.let { tabInitializer.initialize(it, requestHeaders) }
    }

    /**
     * Initialize the preference driven settings of the WebView. This method must be called whenever
     * the preferences are changed within SharedPreferences.
     */
    @SuppressLint("NewApi", "SetJavaScriptEnabled")
    fun initializePreferences() {
        val settings = webView?.settings ?: return

        lightningWebClient.updatePreferences()

        if (userPreferences.doNotTrackEnabled) {
            requestHeaders[HEADER_DNT] = "1"
        } else {
            requestHeaders.remove(HEADER_DNT)
        }

        if (userPreferences.removeIdentifyingHeadersEnabled) {
            requestHeaders[HEADER_REQUESTED_WITH] = ""
            requestHeaders[HEADER_WAP_PROFILE] = ""
        } else {
            requestHeaders.remove(HEADER_REQUESTED_WITH)
            requestHeaders.remove(HEADER_WAP_PROFILE)
        }

        settings.defaultTextEncodingName = userPreferences.textEncoding
        setColorMode(userPreferences.renderingMode)

        if (!isIncognito) {
            settings.setGeolocationEnabled(userPreferences.locationEnabled)
        } else {
            settings.setGeolocationEnabled(false)
        }


        settings.saveFormData = userPreferences.savePasswordsEnabled && !isIncognito

        if (userPreferences.javaScriptEnabled) {
            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
        } else {
            settings.javaScriptEnabled = false
            settings.javaScriptCanOpenWindowsAutomatically = false
        }

        if (userPreferences.textReflowEnabled) {
            settings.layoutAlgorithm = LayoutAlgorithm.NARROW_COLUMNS
            try {
                settings.layoutAlgorithm = LayoutAlgorithm.TEXT_AUTOSIZING
            } catch (e: Exception) {
                // This shouldn't be necessary, but there are a number
                // of KitKat devices that crash trying to set this
                logger.log(TAG, "Problem setting LayoutAlgorithm to TEXT_AUTOSIZING")
            }
        } else {
            settings.layoutAlgorithm = LayoutAlgorithm.NORMAL
        }

        if (!isIncognito) {
            settings.setSupportMultipleWindows(userPreferences.popupsEnabled)
        } else {
            settings.setSupportMultipleWindows(false)
        }

        settings.useWideViewPort = userPreferences.useWideViewportEnabled
        settings.loadWithOverviewMode = userPreferences.overviewModeEnabled
        settings.textZoom = when (userPreferences.textSize) {
            0 -> 200
            1 -> 150
            2 -> 125
            3 -> 100
            4 -> 75
            5 -> 50
            else -> throw IllegalArgumentException("Unsupported text size")
        }

        CookieManager.getInstance().setAcceptThirdPartyCookies(
            webView,
            !userPreferences.blockThirdPartyCookiesEnabled
        )
    }

    @SuppressLint("NewApi")
    private fun WebView.initializeSettings() {
        settings.apply {
            mediaPlaybackRequiresUserGesture = true

            if (API >= Build.VERSION_CODES.LOLLIPOP && !isIncognito) {
                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            } else if (API >= Build.VERSION_CODES.LOLLIPOP) {
                // We're in Incognito mode, reject
                mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            }

            domStorageEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT
            databaseEnabled = true

            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            allowContentAccess = true
            allowFileAccess = true
            allowFileAccessFromFileURLs = false
            allowUniversalAccessFromFileURLs = false

            val agent = System.getProperty("http.agent")
            Log.e(TAG, "initializePreferences: $agent")
            userAgentString = agent

//            getPathObservable("appcache")
//                .subscribeOn(databaseScheduler)
//                .observeOn(mainScheduler)
//                .subscribe { file ->
//                    setAppCachePath(file.path)
//                }
//
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
//                getPathObservable("geolocation")
//                    .subscribeOn(databaseScheduler)
//                    .observeOn(mainScheduler)
//                    .subscribe { file ->
//                        setGeolocationDatabasePath(file.path)
//                    }
//            }
        }

    }

    private fun getPathObservable(subFolder: String) = Single.fromCallable {
        activity.getDir(subFolder, 0)
    }

    /**
     * This method is used to toggle the user agent between desktop and the current preference of
     * the user.
     */
    fun toggleDesktopUA() {
        if (!toggleDesktop) {
            webView?.settings?.userAgentString = DESKTOP_USER_AGENT
        } else {
            setUserAgent(userPreferences.userAgentChoice)
        }

        toggleDesktop = !toggleDesktop
    }

    /**
     * This method sets the user agent of the current tab. There are four options, 1, 2, 3, 4.
     *
     * 1. use the default user agent
     * 2. use the desktop user agent
     * 3. use the mobile user agent
     * 4. use a custom user agent, or the default user agent if none was set.
     *
     * @param choice  the choice of user agent to use, see above comments.
     */
    @SuppressLint("NewApi")
    private fun setUserAgent(choice: Int) {
        val settings = webView?.settings ?: return

        when (choice) {
            1 -> settings.userAgentString = MOBILE_USER_AGENT
            2 -> settings.userAgentString = DESKTOP_USER_AGENT
//            3 -> settings.userAgentString = MOBILE_USER_AGENT
//            4 -> {
//                var ua = userPreferences.userAgentString
//                if (ua.isEmpty()) {
//                    ua = " "
//                }
//                settings.userAgentString = ua
//            }
        }
    }

    fun saveState(): Bundle = Bundle(ClassLoader.getSystemClassLoader()).also {
        webView?.saveState(it)
    }

    fun onPause() {
        webView?.onPause()
        logger.log(TAG, "WebView onPause: " + webView?.id)
    }

    fun onResume() {
        webView?.onResume()
    }

    fun stopLoading() {
        webView?.stopLoading()
    }

    private fun setHardwareRendering() {
        webView?.setLayerType(View.LAYER_TYPE_HARDWARE, paint)
    }

    private fun setNormalRendering() {
        webView?.setLayerType(View.LAYER_TYPE_NONE, null)
    }

    fun setSoftwareRendering() {
        webView?.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    private fun setColorMode(mode: Int) {
        invertPage = false
        when (mode) {
            0 -> {
                paint.colorFilter = null
                // setSoftwareRendering(); // Some devices get segfaults
                // in the WebView with Hardware Acceleration enabled,
                // the only fix is to disable hardware rendering
                setNormalRendering()
                invertPage = false
            }
            1 -> {
                val filterInvert = ColorMatrixColorFilter(
                    negativeColorArray
                )
                paint.colorFilter = filterInvert
                setHardwareRendering()

                invertPage = true
            }
            2 -> {
                val cm = ColorMatrix()
                cm.setSaturation(0f)
                val filterGray = ColorMatrixColorFilter(cm)
                paint.colorFilter = filterGray
                setHardwareRendering()
            }
            3 -> {
                val matrix = ColorMatrix()
                matrix.set(negativeColorArray)
                val matrixGray = ColorMatrix()
                matrixGray.setSaturation(0f)
                val concat = ColorMatrix()
                concat.setConcat(matrix, matrixGray)
                val filterInvertGray = ColorMatrixColorFilter(concat)
                paint.colorFilter = filterInvertGray
                setHardwareRendering()

                invertPage = true
            }

            4 -> {
                val increaseHighContrast = ColorMatrixColorFilter(increaseContrastColorArray)
                paint.colorFilter = increaseHighContrast
                setHardwareRendering()
            }
        }

    }

    /**
     * Pauses the JavaScript timers of the
     * WebView instance, which will trigger a
     * pause for all WebViews in the app.
     */
    fun pauseTimers() {
        webView?.pauseTimers()
        logger.log(TAG, "Pausing JS timers")
    }

    /**
     * Resumes the JavaScript timers of the
     * WebView instance, which will trigger a
     * resume for all WebViews in the app.
     */
    fun resumeTimers() {
        Log.e("ttt", "resumeTimers: url = ${webView?.url}")
        webView?.resumeTimers()
        logger.log(TAG, "Resuming JS timers")
    }

    /**
     * Requests focus down on the WebView instance
     * if the view does not already have focus.
     */
    fun requestFocus() {
        if (webView?.hasFocus() == false) {
            webView?.requestFocus()
        }
    }

    /**
     * Sets the visibility of the WebView to either
     * View.GONE, View.VISIBLE, or View.INVISIBLE.
     * other values passed in will have no effect.
     *
     * @param visible the visibility to set on the WebView.
     */
    fun setVisibility(visible: Int) {
        webView?.visibility = visible
    }

    /**
     * Tells the WebView to reload the current page.
     * If the proxy settings are not ready then the
     * this method will not have an affect as the
     * proxy must start before the load occurs.
     */
    fun reload() {
        // Check if configured proxy is available
//        if (!proxyUtils.isProxyReady(activity)) {
//            // User has been notified
//            return
//        }

        webView?.reload()
    }

    /**
     * Finds all the instances of the text passed to this
     * method and highlights the instances of that text
     * in the WebView.
     *
     * @param text the text to search for.
     */
    @SuppressLint("NewApi")
    fun find(text: String): FindResults {
        webView?.findAllAsync(text)

        return object : FindResults {
            override fun nextResult() {
                webView?.findNext(true)
            }

            override fun previousResult() {
                webView?.findNext(false)
            }

            override fun clearResults() {
                webView?.clearMatches()
            }
        }
    }

    /**
     * Notify the tab to shutdown and destroy
     * its WebView instance and to remove the reference
     * to it. After this method is called, the current
     * instance of the LightningView is useless as
     * the WebView cannot be recreated using the public
     * api.
     */
    // TODO fix bug where WebView.destroy is being called before the tab
    // is removed and would cause a memory leak if the parent check
    // was not in place.
    fun onDestroy() {
//        networkDisposable.dispose()
        webView?.let { tab ->
            // Check to make sure the WebView has been removed
            // before calling destroy() so that a memory leak is not created
            val parent = tab.parent as? ViewGroup
            if (parent != null) {
                logger.log(TAG, "WebView was not detached from window before onDestroy")
                parent.removeView(webView)
            }
            tab.stopLoading()
            tab.onPause()
            tab.clearHistory()
            tab.visibility = View.GONE
            tab.removeAllViews()
            tab.destroyDrawingCache()
            tab.destroy()

            webView = null
        }
    }

    /**
     * Tell the WebView to navigate backwards
     * in its history to the previous page.
     */
    fun goBack() {
        webView?.goBack()
    }

    /**
     * Tell the WebView to navigate forwards
     * in its history to the next page.
     */
    fun goForward() {
        webView?.goForward()
    }

    /**
     * Notifies the [WebView] whether the network is available or not.
     */
    private fun setNetworkAvailable(isAvailable: Boolean) {
        webView?.setNetworkAvailable(isAvailable)
    }

    /**
     * Handles a long click on the page and delegates the URL to the
     * proper dialog if it is not null, otherwise, it tries to get the
     * URL using HitTestResult.
     *
     * @param url the url that should have been obtained from the WebView touch node
     * thingy, if it is null, this method tries to deal with it and find
     * a workaround.
     */
    private fun longClickPage(url: String?) {
        val result = webView?.hitTestResult
        val currentUrl = webView?.url
        val newUrl = result?.extra

        if (currentUrl != null && UrlUtils.isSpecialUrl(currentUrl)) {
            if (UrlUtils.isHistoryUrl(currentUrl)) {
                if (url != null) {
                    dialogBuilder.showLongPressedHistoryLinkDialog(activity, uiController, url)
                } else if (newUrl != null) {
                    dialogBuilder.showLongPressedHistoryLinkDialog(activity, uiController, newUrl)
                }
            } else if (UrlUtils.isBookmarkUrl(currentUrl)) {
                if (url != null) {
                    dialogBuilder.showLongPressedDialogForBookmarkUrl(activity, uiController, url)
                } else if (newUrl != null) {
                    dialogBuilder.showLongPressedDialogForBookmarkUrl(
                        activity,
                        uiController,
                        newUrl
                    )
                }
            } else if (UrlUtils.isDownloadsUrl(currentUrl)) {
                if (url != null) {
                    dialogBuilder.showLongPressedDialogForDownloadUrl(activity, uiController, url)
                } else if (newUrl != null) {
                    dialogBuilder.showLongPressedDialogForDownloadUrl(
                        activity,
                        uiController,
                        newUrl
                    )
                }
            }
        } else {
            if (url != null) {
                if (result != null) {
                    if (result.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE || result.type == WebView.HitTestResult.IMAGE_TYPE) {
                        dialogBuilder.showLongPressImageDialog(
                            activity,
                            uiController,
                            url,
                            userAgent
                        )
                    } else {
                        dialogBuilder.showLongPressLinkDialog(activity, uiController, url)
                    }
                } else {
                    dialogBuilder.showLongPressLinkDialog(activity, uiController, url)
                }
            } else if (newUrl != null) {
                if (result.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE || result.type == WebView.HitTestResult.IMAGE_TYPE) {
                    dialogBuilder.showLongPressImageDialog(
                        activity,
                        uiController,
                        newUrl,
                        userAgent
                    )
                } else {
                    dialogBuilder.showLongPressLinkDialog(activity, uiController, newUrl)
                }
            }
        }
    }

    /**
     * Determines whether or not the WebView can go
     * backward or if it as the end of its history.
     *
     * @return true if the WebView can go back, false otherwise.
     */
    fun canGoBack(): Boolean = webView?.canGoBack() == true

    /**
     * Determine whether or not the WebView can go
     * forward or if it is at the front of its history.
     *
     * @return true if it can go forward, false otherwise.
     */
    fun canGoForward(): Boolean = webView?.canGoForward() == true

    /**
     * Loads the URL in the WebView. If the proxy settings
     * are still initializing, then the URL will not load
     * as it is necessary to have the settings initialized
     * before a load occurs.
     *
     * @param url the non-null URL to attempt to load in
     * the WebView.
     */
    fun loadUrl(url: String) {
        // Check if configured proxy is available
//        if (!proxyUtils.isProxyReady(activity)) {
//            return
//        }

        Log.e(TAG, "loadUrl: $url")
        webView?.loadUrl(url, requestHeaders)
    }

    /**
     * The OnTouchListener used by the WebView so we can
     * get scroll events and show/hide the action bar when
     * the page is scrolled up/down.
     */
    private inner class TouchListener : OnTouchListener {

        internal var location: Float = 0f
        internal var y: Float = 0f
        internal var action: Int = 0

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(view: View?, arg1: MotionEvent): Boolean {
            if (view == null) return false

            if (!view.hasFocus()) {
                view.requestFocus()
            }
            action = arg1.action
            y = arg1.y
            if (action == MotionEvent.ACTION_DOWN) {
                location = y
            } else if (action == MotionEvent.ACTION_UP) {
                val distance = y - location
                if (distance > SCROLL_UP_THRESHOLD && view.scrollY < SCROLL_UP_THRESHOLD) {
                    uiController.showActionBar()
                } else if (distance < -SCROLL_UP_THRESHOLD) {
//                    uiController.hideActionBar()
                    uiController.showActionBar()
                }
                location = 0f
            }
            gestureDetector.onTouchEvent(arg1)
            checkInstagram = true;

            return false
        }
    }

    /**
     * The SimpleOnGestureListener used by the [TouchListener]
     * in order to delegate show/hide events to the action bar when
     * the user flings the page. Also handles long press events so
     * that we can capture them accurately.
     */
    private inner class CustomGestureListener : SimpleOnGestureListener() {

        /**
         * Without this, onLongPress is not called when user is zooming using
         * two fingers, but is when using only one.
         *
         *
         * The required behaviour is to not trigger this when the user is
         * zooming, it shouldn't matter how much fingers the user's using.
         */
        private var canTriggerLongPress = true

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val power = (velocityY * 100 / maxFling).toInt()
            if (power < -10) {
//                uiController.hideActionBar()
                uiController.showActionBar()
            } else if (power > 15) {
                uiController.showActionBar()
            }
            return super.onFling(e1, e2, velocityX, velocityY)
        }

        override fun onLongPress(e: MotionEvent) {
            if (canTriggerLongPress) {
                val msg = webViewHandler.obtainMessage()
                if (msg != null) {
                    msg.target = webViewHandler
                    webView?.requestFocusNodeHref(msg)
                }
            }
        }

        /**
         * Is called when the user is swiping after the doubletap, which in our
         * case means that he is zooming.
         */
        override fun onDoubleTapEvent(e: MotionEvent): Boolean {
            canTriggerLongPress = false
            return false
        }

        /**
         * Is called when something is starting being pressed, always before
         * onLongPress.
         */
        override fun onShowPress(e: MotionEvent) {
            canTriggerLongPress = true
        }
    }

    /**
     * A Handler used to get the URL from a long click
     * event on the WebView. It does not hold a hard
     * reference to the WebView and therefore will not
     * leak it if the WebView is garbage collected.
     */
    private class WebViewHandler(view: LightningView) : Handler() {

        private val reference: WeakReference<LightningView> = WeakReference(view)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val url = msg.data.getString("url")

            reference.get()?.longClickPage(url)
        }
    }

    companion object {

        private const val TAG = "LightningView"

        const val HEADER_REQUESTED_WITH = "X-Requested-With"
        const val HEADER_WAP_PROFILE = "X-Wap-Profile"
        private const val HEADER_DNT = "DNT"

        private val API = android.os.Build.VERSION.SDK_INT
        private val SCROLL_UP_THRESHOLD = Utils.dpToPx(10f)

        private val negativeColorArray = floatArrayOf(
            -1.0f, 0f, 0f, 0f, 255f, // red
            0f, -1.0f, 0f, 0f, 255f, // green
            0f, 0f, -1.0f, 0f, 255f, // blue
            0f, 0f, 0f, 1.0f, 0f // alpha
        )
        private val increaseContrastColorArray = floatArrayOf(
            2.0f, 0f, 0f, 0f, -160f, // red
            0f, 2.0f, 0f, 0f, -160f, // green
            0f, 0f, 2.0f, 0f, -160f, // blue
            0f, 0f, 0f, 1.0f, 0f // alpha
        )
    }


    @JavascriptInterface
    fun onProcessXVideo(commonData: String) {
        val formatScript = commonData.replace("\\", "")
        val thumbPattern = Pattern.compile("(setThumbUrl)(.+?)(.+?)\'")
        val thumbMatcher = thumbPattern.matcher(formatScript)
        var thumbUrl = ""
        if (thumbMatcher.find()) {
            parseXVideo(formatScript)
//            if (!PrefUtils.getBoolean(activity, Constants.FIRST_XXVIDEO)) {
//                PrefUtils.putBoolean(activity, Constants.FIRST_XXVIDEO, true)
//                PrefUtils.putString(activity, Constants.LINK_SAVE_XXVIDEO, thumbUrl)
//                parseXVideo(formatScript)
//            } else {
//                if (thumbUrl != PrefUtils.getString(activity, Constants.LINK_SAVE_XXVIDEO)) {
//                    PrefUtils.putString(activity, Constants.LINK_SAVE_XXVIDEO, thumbUrl)
//                    parseXVideo(formatScript)
//                }
//            }
        }
    }

    private fun parseXVideo(formatScript: CharSequence) {
        Log.e("ttt", "parseXVideo: link $formatScript")
        val urlListDownload = ArrayList<VideoInfo>()
        val titlePattern = Pattern.compile("(setVideoTitle)(.+?)(.+?)\'")
        val titleMatcher = titlePattern.matcher(formatScript)
        var title = ""
        if (titleMatcher.find()) {
            title = titleMatcher.group()
            title = title.substring(15, title.length - 1)
            title = title.replace(" ", "_")
        }

        val lowQualityPattern = Pattern.compile("(setVideoUrlLow)(.+?)(.+?)\'")
        val lowQualityMatcher = lowQualityPattern.matcher(formatScript)
        var lowUrl = ""
        val sizeUrl: Long = 0
        if (lowQualityMatcher.find()) {
            lowUrl = lowQualityMatcher.group()
            lowUrl = lowUrl.substring(16, lowUrl.length - 1)
            val videoInfo = VideoInfo().apply {
                url = lowUrl
                this.title = title
                format = "480P"
                ext = ".mp4"
                fileSize = sizeUrl
            }

            urlListDownload.add(videoInfo)
        }

        val highQualityPattern = Pattern.compile("(setVideoUrlHigh)(.+?)(.+?)\'")
        val highQualityMatcher = highQualityPattern.matcher(formatScript)
        var highUrl = ""
        if (highQualityMatcher.find()) {
            highUrl = highQualityMatcher.group()
            highUrl = highUrl.substring(17, highUrl.length - 1)
            val videoInfo1 = VideoInfo().apply {
                url = highUrl
                this.title = title
                format = "720P"
                ext = ".mp4"
                fileSize = sizeUrl
            }
            urlListDownload.add(videoInfo1)
        }
        EventBus.getDefault().postSticky(urlListDownload)
    }

    @JavascriptInterface
    fun loadVideoPornHub(path: String) {
        Log.e("ttt", "linkVideoPornHub: $path")
        try {
            val parentJSONObject = JSONObject(path)
            val title: String? = parentJSONObject.getString("video_title")
            title?.replace(" ", "_")
            val thumb: String? = parentJSONObject.getString("image_url")
            val jsonArray = JSONObject(path).getJSONArray("mediaDefinitions")
            val length = jsonArray.length()
            var url: String? = null
            for (i in 0 until length) {
                val jsonObject = jsonArray.getJSONObject(i)
                val link = jsonObject.getString("videoUrl")
                if (link.contains("get_media")) {
                    url = link
                    break
                }
            }
            if (url != null) {
                parsePornhub(url, title, thumb)
//                if (!PrefUtils.getBoolean(activity, Constants.FIRST_PORNHUB)) {
//                    PrefUtils.putBoolean(activity, Constants.FIRST_PORNHUB, true)
//                    PrefUtils.putString(activity, Constants.LINK_SAVE_PORNHUB, url)
//                    parsePornhub(url, title, thumb)
//                } else {
//                    if (url != PrefUtils.getString(activity, Constants.LINK_SAVE_PORNHUB)) {
//                        PrefUtils.putString(activity, Constants.LINK_SAVE_PORNHUB, url)
//                        parsePornhub(url, title, thumb)
//                    }
//                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parsePornhub(url: String, title: String?, thumb: String?) {
        val urlListDownload = ArrayList<VideoInfo>()

        AndroidNetworking.get(url)
            .addHeaders("Cookie", CookieManager.getInstance().getCookie(url))
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONArray(object : JSONArrayRequestListener {
                override fun onResponse(response: JSONArray?) {
                    Log.e("ttt", "fjhjfhjsdfsdhf $response")

                    try {
                        val length = response?.length() ?: 0
                        for (i in 0 until length) {
                            val jsonObject = response!!.getJSONObject(i)
                            val quality = jsonObject.getString("quality") + "P"
                            val link = jsonObject.getString("videoUrl")
                            val videoInfo = VideoInfo().apply {
                                this.url = link
                                this.thumbnail = thumbnail
                                this.title = title
                                this.format = quality
                                this.ext = ".mp4"
                            }
                            urlListDownload.add(videoInfo)
                        }
                        EventBus.getDefault().postSticky(urlListDownload)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(error: ANError) {
                    error.printStackTrace()
                }
            })
    }
}
