package com.bangbangcoding.screenmirror.web.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.NotificationManager
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.style.CharacterStyle
import android.text.style.ParagraphStyle
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.Transformation
import android.view.inputmethod.EditorInfo
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.adapter.CallBack
import com.bangbangcoding.screenmirror.adapter.ShortcutAdapter
import com.bangbangcoding.screenmirror.databinding.ActivityWebBinding
import com.bangbangcoding.screenmirror.databinding.DialogAddNewShortcutBinding
import com.bangbangcoding.screenmirror.db.model.entity.Shortcut
import com.bangbangcoding.screenmirror.db.viewmodel.WebViewModel
import com.bangbangcoding.screenmirror.db.viewmodel.WebViewModelFactory
import com.bangbangcoding.screenmirror.web.KunApplication
import com.bangbangcoding.screenmirror.web.controller.UIController
import com.bangbangcoding.screenmirror.web.data.bookmark.BookmarkRepository
import com.bangbangcoding.screenmirror.web.data.history.HistoryRepository
import com.bangbangcoding.screenmirror.web.data.local.Bookmark
import com.bangbangcoding.screenmirror.web.data.local.model.DomainAllow
import com.bangbangcoding.screenmirror.web.data.local.model.Media
import com.bangbangcoding.screenmirror.web.data.remote.request.ErrorType
import com.bangbangcoding.screenmirror.web.di.module.DatabaseScheduler
import com.bangbangcoding.screenmirror.web.di.module.MainHandler
import com.bangbangcoding.screenmirror.web.di.module.MainScheduler
import com.bangbangcoding.screenmirror.web.html.history.HistoryPageFactory
import com.bangbangcoding.screenmirror.web.ssl.SSLState
import com.bangbangcoding.screenmirror.web.ui.base.BaseActivity
import com.bangbangcoding.screenmirror.web.ui.dialog.*
import com.bangbangcoding.screenmirror.web.ui.feature.bookmarks.BookmarksFragment
import com.bangbangcoding.screenmirror.web.ui.feature.bookmarks.BookmarksView
import com.bangbangcoding.screenmirror.web.ui.feature.feedback.FeedbackFragment
import com.bangbangcoding.screenmirror.web.ui.feature.history.HistoryFragment
import com.bangbangcoding.screenmirror.web.ui.feature.mange_tab.OnTabHandler
import com.bangbangcoding.screenmirror.web.ui.feature.mange_tab.TabsFragment
import com.bangbangcoding.screenmirror.web.ui.home.TabsManager
import com.bangbangcoding.screenmirror.web.ui.home.TabsView
import com.bangbangcoding.screenmirror.web.ui.home.TopPageAdapter
import com.bangbangcoding.screenmirror.web.ui.model.apis_models.VideoInfo
import com.bangbangcoding.screenmirror.web.ui.paste.fragment.BuzzPasteLinkFragment
import com.bangbangcoding.screenmirror.web.ui.preference.UserPreferences
import com.bangbangcoding.screenmirror.web.ui.progress.DownloadFragment
import com.bangbangcoding.screenmirror.web.ui.search.SearchEngineProvider
import com.bangbangcoding.screenmirror.web.ui.search.SuggestionsAdapter
import com.bangbangcoding.screenmirror.web.ui.setting.SettingFragment
import com.bangbangcoding.screenmirror.web.ui.studio.multidownload.DownloadManager
import com.bangbangcoding.screenmirror.web.ui.widget.*
import com.bangbangcoding.screenmirror.web.ui.widget.SearchView
import com.bangbangcoding.screenmirror.web.ui.widget.interpolator.BezierDecelerateInterpolator
import com.bangbangcoding.screenmirror.web.utils.*
import com.bangbangcoding.screenmirror.web.utils.ext.*
import com.bangbangcoding.screenmirror.web.utils.fragment.FragmentFactory
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.shashank.sony.fancytoastlib.FancyToast
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import javax.inject.Inject

class WebActivity : BaseActivity(), UIController {
    companion object {
        private const val TAG_BOOKMARK_FRAGMENT = "TAG_BOOKMARK_FRAGMENT"
        private const val TAG_TABS_FRAGMENT = "TAG_TABS_FRAGMENT"
        const val INTENT_PANIC_TRIGGER = "info.guardianproject.panic.action.TRIGGER"
        const val INTENT_ORIGIN = "URL_INTENT_ORIGIN"

        private val MATCH_PARENT =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
    }

    private val webViewModel: WebViewModel by viewModels {
        WebViewModelFactory((this.application as KunApplication).repository)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var fragmentFactory: FragmentFactory

    @Inject
    lateinit var clipboardManager: ClipboardManager

    @Inject
    @field:MainHandler
    lateinit var mainHandler: Handler

    private lateinit var mainAdapter: MainPagerAdapter

    private lateinit var binding: ActivityWebBinding

    private var searchView: SearchView? = null
    private var searchContainer: LinearLayout? = null
    private var tvTbTitle: TextView? = null
    private var searchHomeView: SearchView? = null

    private var tabsView: TabsView? = null

    private var bookmarksView: BookmarksView? = null

    private var tabHandler: OnTabHandler? = null

    private var webPageBitmap: Bitmap? = null

    //Image
    private val backgroundDrawable = ColorDrawable()
    private var iconDrawable: Drawable? = null
    private var deleteIconDrawable: Drawable? = null
    private var refreshIconDrawable: Drawable? = null
    private var clearIconDrawable: Drawable? = null
    private var sslDrawable: Drawable? = null
    private var animationDrawable: AnimationDrawable? = null

    private var backgroundColor: Int = 0

    //Init
    @Inject
    lateinit var tabsManager: TabsManager

    @Inject
    lateinit var userPreferences: UserPreferences

    // The singleton BookmarkManager
    @Inject
    lateinit var bookmarkRepository: BookmarkRepository

    @Inject
    lateinit var historyRepository: HistoryRepository

    @Inject
    lateinit var homePageInitializer: HomePageInitializer

    @Inject
    lateinit var historyPageFactory: HistoryPageFactory

    @Inject
    lateinit var searchEngineProvider: SearchEngineProvider

    @Inject
    @DatabaseScheduler
    lateinit var databaseScheduler: Scheduler

    @Inject
    lateinit var searchBoxModel: SearchBoxModel

    @Inject
    @MainScheduler
    lateinit var mainScheduler: Scheduler

    private var currentTabView: View? = null

    private var themeId: Int = 0

    private var suggestionsAdapter: SuggestionsAdapter? = null

    // Primitives
    private var isFullScreen: Boolean = false
    private var isDarkTheme: Boolean = false
    private var shouldShowTabsInDrawer: Boolean = false
    private var swapBookmarksAndTabs: Boolean = false
    private var searchText: String? = null
    private var originalOrientation: Int = 0

    private var shareMenuItem: MenuItem? = null
    private var adMenuItem: MenuItem? = null
    private var mVideoDetects: List<VideoInfo>? = null
    private var mVideoDownload: VideoInfo? = null

    private lateinit var topPageAdapter: TopPageAdapter
    private var mLastClickTime: Long = 0
    private var mClickBackCount: Int = 0

    private var mInterstitialAd: InterstitialAd? = null
    private var isShowAd = false
    private var mTypeAds = 0
    private lateinit var mDownloadManager: DownloadManager

    private fun updateCookiePreference(): Completable = Completable.fromAction {
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(userPreferences.cookiesEnabled)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clearNotify()
        Constants.setConstants()
        //Set notification
        AlarmUtil.saveAlarm(this, true)

        checkHasClipboard()

        mainViewModel =
            ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        loadNativeAds()
        loadInterstitialAd()

        requestPermissionLauncher1.launch(perms)
        tabsManager.addTabNumberChangedListener {
            updateTabNumber(tabsManager.size())
        }


        mDownloadManager = DownloadManager.getInstance()
        mDownloadManager.init(this)
        updateDownloadingNumber(mDownloadManager.currentDownloadCount)

        mDownloadManager.setCountListener {
            updateDownloadingNumber(mDownloadManager.currentDownloadCount)
        }

        binding.tb.btnBack.setOnClickListener {
            this@WebActivity.finish()
        }

        topPageAdapter = TopPageAdapter(ArrayList(0),
            object : TopPageAdapter.TopPagesListener {
                override fun onItemClicked(pageInfo: DomainAllow) {
                    launchToFragment(BuzzPasteLinkFragment::class.java, pageInfo.domain!!)

                }

                override fun onItemDelete(pageInfo: DomainAllow) {
                    //todo
                }
            })
        initialize(savedInstanceState)
        setActionNav()
        loadNativeAdsBack()
    }

    private fun updateDownloadingNumber(size: Int) {
        Log.e("ttt", "Downloading All: " + size)
        Log.e("ttt", "Downloading Current: " + size)
        if (size > 0) {
            binding.navigator.tvNumDownloading.visibility = View.VISIBLE
            binding.navigator.tvNumDownloading.text = size.toString()
        } else {
            binding.navigator.tvNumDownloading.visibility = View.GONE
        }
    }

    private fun loadNativeAds() {
        val adLoader = AdLoader.Builder(this, Constants.ADS_NATIVE_ID)
            .forNativeAd { ad: NativeAd ->
                // Show the ad.
                val styles = NativeTemplateStyle.Builder().build()
                binding.myTemplate.setStyles(styles)
                binding.myTemplate.setNativeAd(ad)
                binding.myTemplate.isVisible = true
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    binding.myTemplate.visibility = View.GONE
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    // Methods in the NativeAdOptions.Builder class can be
                    // used here to specify individual options settings.
                    .build()
            )
            .build()

        adLoader.loadAds(AdRequest.Builder().build(), 3)
    }

    private fun initialize(savedInstanceState: Bundle?) {
        initializeToolbarHeight(resources.configuration)
        setSupportActionBar(binding.tb.toolbar)
        val actionBar = requireNotNull(supportActionBar)

        isDarkTheme = true
        shouldShowTabsInDrawer = userPreferences.showTabsInDrawer
        swapBookmarksAndTabs = userPreferences.bookmarksAndTabsSwapped

        // Drawer stutters otherwise
        binding.rightDrawer.setLayerType(View.LAYER_TYPE_NONE, null)

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) = Unit

            override fun onDrawerOpened(drawerView: View) = Unit

            override fun onDrawerClosed(drawerView: View) = Unit

            override fun onDrawerStateChanged(newState: Int) {
                if (newState == DrawerLayout.STATE_DRAGGING) {
                    binding.rightDrawer.setLayerType(View.LAYER_TYPE_HARDWARE, null)
                } else if (newState == DrawerLayout.STATE_IDLE) {
                    binding.rightDrawer.setLayerType(View.LAYER_TYPE_NONE, null)
                }
            }
        })

        val adapter = ShortcutAdapter(webViewModel,this@WebActivity, object : CallBack{
            override fun deleteShortcut(shortcut: Shortcut) {
                webViewModel.deleteShortcut(shortcut)
            }

            override fun onClickShortcut(shortcut: Shortcut) {
                searchTheWeb(shortcut.url)
            }

            override fun addNewShortcut() {
              this@WebActivity.addNewShortcut()
            }

        })
        webViewModel.allShortcut.observe(this) {
            it?.let { adapter.submitList(it) }
        }
        binding.rvTopPages.apply {
            this.layoutManager = GridLayoutManager(this@WebActivity, 4)
            this.adapter = adapter
        }

        binding.txtEditShortcut.setOnClickListener {
            webViewModel.visibility.value = webViewModel.visibility.value != true
        }

        webViewModel.visibility.observe(this) {
            if (!it) {
                binding.txtEditShortcut.text = resources.getText(R.string.edit)
            } else {
                binding.txtEditShortcut.text = resources.getText(R.string.finish)
            }
        }

        webPageBitmap = ThemeUtils.getThemedBitmap(this, R.drawable.ic_webpage, isDarkTheme)

        val fragmentManager = supportFragmentManager

        val tabsFragment: TabsFragment? =
            fragmentManager.findFragmentByTag(TAG_TABS_FRAGMENT) as? TabsFragment
        val bookmarksFragment: BookmarksFragment? =
            fragmentManager.findFragmentByTag(TAG_BOOKMARK_FRAGMENT) as? BookmarksFragment

        tabsView =
            tabsFragment ?: TabsFragment.newInstance(isIncognito())

        if (bookmarksFragment != null) {
            fragmentManager.beginTransaction().remove(bookmarksFragment).commit()
        }

        bookmarksView = bookmarksFragment ?: BookmarksFragment.newInstance(isIncognito())

        fragmentManager.executePendingTransactions()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.right_drawer, bookmarksView as Fragment, TAG_BOOKMARK_FRAGMENT)
            .commit()

        // set display options of the ActionBar
        actionBar.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayShowHomeEnabled(false)
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setCustomView(R.layout.toolbar_content)

        val customView = actionBar.customView
        customView.layoutParams = customView.layoutParams.apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.MATCH_PARENT
        }

        val iconBounds = Utils.dpToPx(24f)
        backgroundColor = ThemeUtils.getPrimaryColor(this)
        deleteIconDrawable =
            ThemeUtils.getThemedDrawable(this, R.drawable.ic_close_24dp, isDarkTheme).apply {
                setBounds(0, 0, iconBounds, iconBounds)
            }
        refreshIconDrawable =
            ThemeUtils.getThemedDrawable(this, R.drawable.ic_refresh_gray_24dp, isDarkTheme).apply {
                setBounds(0, 0, iconBounds, iconBounds)
            }
        clearIconDrawable =
            ThemeUtils.getThemedDrawable(this, R.drawable.ic_close_24dp, isDarkTheme).apply {
                setBounds(0, 0, iconBounds, iconBounds)
            }

        // create the search EditText in the ToolBar
        searchContainer = customView.findViewById(R.id.search_container)
        tvTbTitle = customView.findViewById(R.id.tvTbTitle)

        searchView = customView.findViewById<SearchView>(R.id.search).apply {
            iconDrawable = refreshIconDrawable
            compoundDrawablePadding = Utils.dpToPx(3f)
            setCompoundDrawablesWithIntrinsicBounds(sslDrawable, null, refreshIconDrawable, null)

            val searchListener = SearchListenerClass(this)
            setOnKeyListener(searchListener)
            onFocusChangeListener = searchListener
            setOnEditorActionListener(searchListener)
            onPreFocusListener = searchListener
            onLongPressListener = searchListener
            addTextChangedListener(searchListener)

            initializeSearchSuggestions(this)
        }

        searchView?.onRightDrawableClickListener = {
            if (it.hasFocus()) {
                it.setText("")
            } else {
                refreshOrStop()
            }
        }

        searchHomeView = binding.tb.searchHome.apply {
            val searchListener = SearchListenerClass(this)
            setOnKeyListener(searchListener)
            onFocusChangeListener = searchListener
            setOnEditorActionListener(searchListener)
            onPreFocusListener = searchListener
            onLongPressListener = searchListener
            addTextChangedListener(searchListener)

            initializeSearchSuggestions(this)
        }
        searchHomeView?.onRightDrawableClickListener = {
            //todo do something for mic handle
            goToSearchEngine(it)
        }

        var intent: Intent? = if (savedInstanceState == null) {
            intent
        } else {
            null
        }

        val launchedFromHistory =
            intent != null && intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY != 0

        if (intent?.action == INTENT_PANIC_TRIGGER) {
//            setIntent(null)
            panicClean()
        } else {
            if (launchedFromHistory) {
//                intent = null
            }
            setupTabs(intent)
//            setIntent(null)
//            proxyUtils.checkForProxy(this)
        }
        setObserver()
    }

    private fun setObserver() {
        mainViewModel.start()
        mainViewModel.listPages.observe(this) {
            Log.e("ttt", "observer listPages: $it")
            topPageAdapter.setData(it)
        }
        mainViewModel.listPagesBlock.observe(this) {
            val paths = it.map { s -> s.domain }
            PrefUtils.saveListPathDomain(this, ArrayList(paths))
        }
    }

    fun updateSslState(sslState: SSLState) {
        sslDrawable = when (sslState) {
            is SSLState.None -> null
            is SSLState.Valid -> {
                val bitmap = DrawableUtils.getImageInsetInRoundedSquare(
                    this,
                    R.drawable.ic_secured,
                    R.color.ssl_secured
                )
                val securedDrawable = BitmapDrawable(resources, bitmap)
                securedDrawable
            }
            is SSLState.Invalid -> {
                val bitmap = DrawableUtils.getImageInsetInRoundedSquare(
                    this,
                    R.drawable.ic_unsecured,
                    R.color.ssl_unsecured
                )
                val unsecuredDrawable = BitmapDrawable(resources, bitmap)
                unsecuredDrawable
            }
        }

        searchView?.setCompoundDrawablesWithIntrinsicBounds(sslDrawable, null, iconDrawable, null)
    }

    private val resultActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Do something after user returned from app settings screen, like showing a Toast.
                result.data?.let {
                    val link = it.getStringExtra("Website")
                    link?.let { l ->
                        loadUrlInCurrentView(l)
                    }
                }
            }
        }

    private fun launchToFragment(fragment: Class<out Fragment>, link: String) {
        resultActivity.launch(
            IntentActivity.getIntent(
                this,
                fragment,
                Bundle().apply {
                    putString("link", link)
                }
            )
        )
    }

    /**
     * method to generate search suggestions for the AutoCompleteTextView from
     * previously searched URLs
     */
    private fun initializeSearchSuggestions(autoCompleteTextView: AutoCompleteTextView) {
        suggestionsAdapter = SuggestionsAdapter(this, isDarkTheme, isIncognito())

        autoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { _, view, _, _ ->
                var url: String? = null
                val urlString = (view.findViewById<View>(R.id.url) as TextView).text

                if (urlString != null) {
                    url = urlString.toString()
                }
                if (url == null || url.startsWith(getString(R.string.suggestion))) {
                    val searchString = (view.findViewById<View>(R.id.title) as TextView).text
                    if (searchString != null) {
                        url = searchString.toString()
                    }
                }
                if (url == null) {
                    return@OnItemClickListener
                }
                if (autoCompleteTextView.id == R.id.searchHome) {
                    autoCompleteTextView.setText("")
                } else {
                    autoCompleteTextView.setText(url)
                }
                searchTheWeb(url)
                AppUtil.hideSoftKeyboard(autoCompleteTextView)
                tabsManager.currentTab?.requestFocus()
            }

        autoCompleteTextView.setSelectAllOnFocus(true)
        autoCompleteTextView.setAdapter<SuggestionsAdapter>(suggestionsAdapter)
    }

    @SuppressLint("CheckResult")
    private fun setupTabs(intent: Intent?) {
        tabsManager.initializeTabs(this, intent, isIncognito())
            .subscribeBy(
                onSuccess = {
                    // At this point we always have at least a tab in the tab manager
                    notifyTabViewInitialized()
                    updateTabNumber(tabsManager.size())
                    tabChanged(tabsManager.positionOf(it))
                }
            )
    }

    private fun notifyTabViewInitialized() {
        Log.e("ttt", "Notify Tabs Initialized")
        tabsView?.tabsInitialized()
    }

    private fun tabChanged(position: Int) {
        if (position < 0 || position >= tabsManager.size()) {
            Log.e("ttt", "tabChanged invalid position: $position")
            return
        }

        Log.e("ttt", "tabChanged: $position")
        onTabChanged(tabsManager.switchToTab(position))
    }

    private fun removeTabView() {
        Log.e("ttt", "Remove the tab view")
        currentTabView.removeFromParent()
        currentTabView = null
//        mainHandler.postDelayed(drawer_layout::closeDrawers, 200)
    }

    private fun onTabChanged(newTab: LightningView?) {
        updateSslState(newTab?.currentSslState() ?: SSLState.None)
        Log.e("ttt", "On tab changed new = $newTab ||| old = ${tabsManager.currentTab}")
        val webView = newTab?.webView

        if (newTab == null) {
            removeTabView()
        } else {
            if (webView == null) {
                removeTabView()
                tabsManager.currentTab?.let {
                    Log.e("ttt", "onTabChanged: pauseTimers ${tabsManager.currentTab?.url}")
                    it.pauseTimers()
                    it.onDestroy()
                }
            } else {
                tabsManager.currentTab?.let {
                    // TODO: Restore this when Google fixes the bug where the WebView is
                    // blank after calling onPause followed by onResume.
                    // currentTab.onPause();
                    it.isForegroundTab = false
                }
                Log.e("ttt", "onTabChanged: resumeTimers ${webView.url}")
                newTab.resumeTimers()
                newTab.onResume()
                newTab.isForegroundTab = true

                updateProgress(newTab.progress)
                setBackButtonEnabled(newTab.canGoBack())
                setForwardButtonEnabled(newTab.canGoForward())
                updateUrl(newTab.url, false)
                setTabView(webView)
                val index = tabsManager.indexOfTab(newTab)
                if (index >= 0) {
                    notifyTabViewChanged(tabsManager.indexOfTab(newTab))
                }
            }
        }

//        currentTab = newTab
//        currentTabView = newTab
    }


    fun setTabView(view: View) {
        if (currentTabView == view) {
            return
        }

        Log.e("ttt", "Setting the tab view $view")

        // Set the background color so the color mode color doesn't show through
        binding.main.contentFrame.setBackgroundColor(Color.BLACK)

        view.removeFromParent()
        currentTabView.removeFromParent()

//        binding.main.contentFrame.addView(view, 0, MATCH_PARENT)


        binding.main.contentFrame.addView(view, 0, MATCH_PARENT)

//        binding.main.contentFrame.addView(webview, 0, MATCH_PARENT)

        if (isFullScreen) {
            view.translationY =
                binding.tb.toolbarLayout.height + binding.tb.toolbarLayout.translationY
        } else {
            view.translationY = 0f
        }

        view.requestFocus()

        currentTabView = view

        showActionBar()
    }

    private fun notifyTabViewChanged(position: Int) {
        Log.e("ttt", "Notify Tab Changed: $position")
        tabsView?.tabChanged(position)
        updateOptionState()
    }

    private fun panicClean() {
        Log.d("DELETE_TAB", "clean")
        Log.e("ttt", "Closing browser")
        tabsManager.newTab(this, NoOpInitializer(), false, userPreferences)
        tabsManager.switchToTab(0)
        tabsManager.clearSavedState()

        historyPageFactory.deleteHistoryPage().subscribe()
        closeBrowser()
//        System.exit(1)
    }

    fun closeBrowser() {
        Log.d("DELETE_TAB", "Clodebrower")
        binding.main.contentFrame.setBackgroundColor(Color.WHITE)
        currentTabView.removeFromParent()
        performExitCleanUp()
        val size = tabsManager.size()
        tabsManager.shutdown()
        currentTabView = null
        for (n in 0 until size) {
            tabsView?.tabRemoved(0)
        }
        finishAffinity()
    }

    private fun performExitCleanUp() {
        val currentTab = tabsManager.currentTab
        if (userPreferences.clearCacheExit && currentTab != null && !isIncognito()) {
            WebUtils.clearCache(currentTab.webView)
            Log.e("ttt", "Cache Cleared")
        }
        if (userPreferences.clearHistoryExitEnabled && !isIncognito()) {
            WebUtils.clearHistory(this, historyRepository, databaseScheduler)
            Log.e("ttt", "History Cleared")
        }
        if (userPreferences.clearCookiesExitEnabled && !isIncognito()) {
            WebUtils.clearCookies()
            Log.e("ttt", "Cookies Cleared")
        }
        if (userPreferences.clearWebStorageExitEnabled && !isIncognito()) {
            WebUtils.clearWebStorage()
            Log.e("ttt", "WebStorage Cleared")
        } else if (isIncognito()) {
            WebUtils.clearWebStorage()     // We want to make sure incognito mode is secure
        }
    }

    /**
     * handle presses on the refresh icon in the search bar, if the page is
     * loading, stop the page, if it is done loading refresh the page.
     * See setIsFinishedLoading and setIsLoading for displaying the correct icon
     */
    private fun refreshOrStop() {
        val currentTab = tabsManager.currentTab
        if (currentTab != null) {
            if (currentTab.progress < 100) {
                currentTab.stopLoading()
            } else {
                currentTab.reload()
            }
        }
    }

    private fun goToSearchEngine(sv: SearchView) {
        val currentTab = tabsManager.currentTab
        val url = sv.text.toString().trim()
        if (currentTab != null) {
            if (!url.isNullOrBlank()) {
                if (UrlUtils.checkURL(url)) {
                    currentTab.loadUrl(url)
                } else {
                    currentTab.loadUrl("${searchEngineProvider.provideSearchEngine().queryUrl}$url")
                }
            } else {
                Log.e(
                    "ttt",
                    "goToSearchEngine: ${searchEngineProvider.provideSearchEngine().queryUrl}"
                )
                currentTab.loadUrl(searchEngineProvider.provideSearchEngine().queryUrl)
            }
            sv.setText("")
        }
    }

    private fun initializeToolbarHeight(configuration: Configuration) =
        binding.clGroup.doOnLayout {
            // TODO externalize the dimensions
            val toolbarSize = if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                // In portrait toolbar should be 56 dp tall
                Utils.dpToPx(56f)
            } else {
                // In landscape toolbar should be 48 dp tall
                Utils.dpToPx(52f)
            }
            binding.tb.toolbar.layoutParams =
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, toolbarSize)
            binding.tb.toolbar.minimumHeight = toolbarSize
            binding.tb.toolbar.doOnLayout { setWebViewTranslation(binding.tb.toolbarLayout.height.toFloat()) }
            binding.tb.toolbar.requestLayout()
        }


    override fun onResume() {
        super.onResume()
        suggestionsAdapter?.let {
            it.refreshPreferences()
            it.refreshBookmarks()
        }
        tabsManager.resumeAll()
        initializePreferences()
        putToolbarInRoot()
        updateOptionState()
    }

    private fun checkHasClipboard() {
        ClipboardUtils.checkHasCopy(this)?.let {
            PrefUtils.getString(this, Constants.PREF_COPY_DATA)?.let { data ->
                if (data == it) {
                    return
                } else {
                    PrefUtils.putString(this, Constants.PREF_COPY_DATA, it)
                }
            } ?: PrefUtils.putString(this, Constants.PREF_COPY_DATA, it)

            BrowserDialog.showPositiveNegativeDialog(
                this,
                R.string.title_open_link_copy,
                R.string.path_link_copy,
                arrayOf(it),
                DialogItem(title = R.string.txt_ok, onClick = {
                    loadUrlInCurrentView(it)
                }),
                DialogItem(title = R.string.action_cancel, onClick = {}),
                onCancel = {}
            )
        }
    }

    private fun putToolbarInRoot() {
        if (binding.tb.toolbarLayout.parent != binding.clGroup) {
            (binding.tb.toolbarLayout.parent as ViewGroup?)?.removeView(binding.tb.toolbarLayout)

            binding.clGroup.addView(binding.tb.toolbarLayout, 0)
            binding.clGroup.requestLayout()
        }
        binding.tb.btnBack.setOnClickListener {
            this@WebActivity.finish()
        }
        setWebViewTranslation(0f)
    }

    private fun overlayToolbarOnWebView() {
        if (binding.clGroup.parent != binding.main.contentFrame) {
            (binding.tb.toolbarLayout.parent as ViewGroup?)?.removeView(binding.tb.toolbarLayout)

            binding.main.contentFrame.addView(binding.tb.toolbarLayout)
            binding.main.contentFrame.requestLayout()
        }
        setWebViewTranslation(binding.tb.toolbarLayout.height.toFloat())
    }

    private fun setWebViewTranslation(translation: Float) =
        if (isFullScreen) {
            currentTabView?.translationY = translation
        } else {
            currentTabView?.translationY = 0f
        }

    override fun onPause() {
        super.onPause()
        saveOpenTabs()
        Log.e("ttt", "onPause")
        tabsManager.pauseAll()
    }

    private fun saveOpenTabs() {
        if (userPreferences.restoreLostTabsEnabled || themeId != userPreferences.useTheme) {
            val handler = Handler()
            handler.postDelayed({
                tabsManager.saveState()
            }, 500)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        unregisterReceiver(onDownloadComplete)
    }

    protected fun isIncognito(): Boolean = false

    private fun initializePreferences() {
        val currentView = tabsManager.currentTab
        isFullScreen = userPreferences.fullScreenEnabled
        val colorMode = userPreferences.colorModeEnabled && !isDarkTheme


        webPageBitmap?.let { webBitmap ->
            if (!isIncognito() && !colorMode && !isDarkTheme) {
                changeToolbarBackground(webBitmap, null)
            } else if (!isIncognito() && currentView != null && !isDarkTheme) {
                changeToolbarBackground(currentView.favicon, null)
            } else if (!isIncognito() && !isDarkTheme) {
                changeToolbarBackground(webBitmap, null)
            }
        }

//        val manager = supportFragmentManager
//        val tabsFragment = manager.findFragmentByTag(TAG_TABS_FRAGMENT) as? TabsFragment
//        tabsFragment?.reinitializePreferences()
//        val bookmarksFragment =
//            manager.findFragmentByTag(TAG_BOOKMARK_FRAGMENT) as? BookmarksFragment
//        bookmarksFragment?.reinitializePreferences()

        setFullscreen(userPreferences.hideStatusBarEnabled, false)

        val currentSearchEngine = searchEngineProvider.provideSearchEngine()
        searchText = currentSearchEngine.queryUrl

        updateCookiePreference().subscribeOn(Schedulers.computation()).subscribe()
//        proxyUtils.updateProxySettings(this)
    }


    private fun clearNotify() {
        Log.e(
            "ttt",
            "clearNotify: time = ${intent?.getBooleanExtra(Constants.Alarm.PREF_TIME_PUSH, false)}"
        )
        if (intent?.getBooleanExtra(Constants.Alarm.PREF_TIME_PUSH, false) == true) {
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(AlarmUtil.NOTIFY_ID_ALARM)
        }
    }

//    override fun onNewIntent(intent: Intent) {
//        super.onNewIntent(intent)
//        if (intent?.action == INTENT_PANIC_TRIGGER) {
//            panicClean()
//        } else {
//            handleNewIntent(intent)
//            super.onNewIntent(intent)
//        }
//    }

    private fun handleNewIntent(intent: Intent?) {
        tabsManager.doAfterInitialization {
            val url = if (intent?.action == Intent.ACTION_WEB_SEARCH) {
                tabsManager.extractSearchFromIntent(intent)
            } else {
                intent?.dataString
            }

            val tabHashCode = intent?.extras?.getInt(INTENT_ORIGIN, 0) ?: 0

            if (tabHashCode != 0 && url != null) {
                tabsManager.getTabForHashCode(tabHashCode)?.loadUrl(url)
            } else if (url != null) {
//                if (URLUtil.isFileUrl(url)) {
//                    showBlockedLocalFileDialog {
//                        newTab(UrlInitializer(url), true)
//                        shouldClose = true
//                        tabsManager.lastTab()?.isNewTab = true
//                    }
//                } else {
                newTab(UrlInitializer(url), true)
//                    shouldClose = true
                tabsManager.lastTab()?.isNewTab = true
//                }
            }
        }
    }

    fun newTab(
        tabInitializer: TabInitializer,
        show: Boolean,
        isIncognito: Boolean = false
    ): Boolean {
        Log.e("ttt", "newTab: $show || $tabInitializer")
        // Limit number of tabs for limited version of app
        if (tabsManager.size() >= 10) {
            snackbar(R.string.max_tabs)
            return false
        }

        val startingTab = tabsManager.newTab(this, tabInitializer, isIncognito, userPreferences)
        if (tabsManager.size() == 1) {
            startingTab.resumeTimers()
        }

        notifyTabViewAdded()
        updateTabNumber(tabsManager.size())

        if (show) {
            onTabChanged(tabsManager.switchToTab(tabsManager.last()))
        }

        return true
    }

    private fun setFullscreen(enabled: Boolean, immersive: Boolean) {
//        hideStatusBar = enabled
//        isImmersiveMode = immersive
//        val window = window
//        val decor = window.decorView
//        if (enabled) {
//            if (immersive) {
//                decor.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        or View.SYSTEM_UI_FLAG_FULLSCREEN
//                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
//            } else {
//                decor.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
//            }
//            window.setFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN
//            )
//        } else {
//            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
//            decor.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
//        }
    }

    private fun updateOptionState() {
        resetTabSelect()
        val currentView = tabsManager.currentTab
        currentView?.focus = true
        updateUrl(currentView?.url, true)
        if (currentView != null) {
            if (currentView.title == getString(R.string.home)) {
                binding.navigator.navigationHistory.isEnabled = true
                binding.navigator.navigationAddNewTab.setImageResource(R.drawable.add_shortcut_disable)
                binding.navigator.navigationAddNewTab.isEnabled = false
                adMenuItem?.isVisible = true
                shareMenuItem?.isVisible = false
                binding.scrollView.visibility = View.VISIBLE
                binding.main.root.visibility = View.GONE
                searchHomeView?.visibility = View.VISIBLE
                searchContainer?.visibility = View.GONE
                tvTbTitle?.visibility = View.VISIBLE
            } else {
                binding.navigator.navigationAddNewTab.isEnabled = true
                binding.navigator.navigationAddNewTab.setImageResource(R.drawable.add_shortcut)
                binding.navigator.navigationBack.isEnabled = currentView.canGoBack()
                binding.navigator.navigationNext.isEnabled = currentView.canGoForward()
                adMenuItem?.isVisible = false
                shareMenuItem?.isVisible = true
                binding.navigator.navigationHistory.isEnabled = true
                binding.scrollView.visibility = View.GONE
                binding.main.root.visibility = View.VISIBLE
                searchHomeView?.visibility = View.GONE
                searchContainer?.visibility = View.VISIBLE
                tvTbTitle?.visibility = View.GONE
            }
        }
    }

    private fun updateTabNumber(number: Int) {
        binding.navigator.tvNumTabs.text = number.toString()
    }

    private val perms = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val requestPermissionLauncher1 =
        registerForActivityResult(RequestMultiplePermissions()) { result ->
            if (result[perms[0]] != null && result[perms[0]]!!) {
                //no-op
            }
        }

    private val requestPermissionLauncherWriteStorage =
        registerForActivityResult(RequestMultiplePermissions()) { result ->
            if (result[perms[0]] != null && result[perms[0]]!!) {
//                downloadVideo(mVideoDownload!!)
                checkSpace(mVideoDownload!!)
            } else {
                snackbar(R.string.txt_please_allow_permission_download)
            }
        }

    private fun setActionNav() {
        binding.navigator.navigationBack.isEnabled = false
        binding.navigator.navigationNext.isEnabled = false
        binding.navigator.navigationBack.setOnClickListener {
            val currentView = tabsManager.currentTab
            if (currentView?.canGoBack() == true) {
                currentView.goBack()
            }
        }
        binding.navigator.navigationNext.setOnClickListener {
            val currentView = tabsManager.currentTab
            if (currentView?.canGoForward() == true) {
                currentView.goForward()
            }
        }
        binding.navigator.navigationFolder.setOnClickListener {
//            startActivity(Intent(this, TestAdsActivity::class.java))
//            startActivity(IntentActivity.getIntent(this, TestAdsFragment::class.java))
            showInterstitial(0)
        }
        binding.navigator.navigationTabs.setOnClickListener {
            mainViewModel.cacheScreenShot(
                this.window.decorView.rootView,
                lightningView = tabsManager.currentTab,
                url = tabsManager.currentTab?.url
            )
            showInterstitial(1)
        }
        binding.navigator.navigationHistory.setOnClickListener {
            showInterstitial(2)
        }
        //todo
        binding.navigator.navigationAddNewTab.setOnClickListener {
            newTab(homePageInitializer, true)
        }
    }


    override fun onBackPressed() {
        Log.e("ttt", "onBackPressed: ")
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            binding.drawerLayout.closeDrawers()
            return
        }
        val currentView = tabsManager.currentTab
        if (currentView?.canGoBack() == true) {
            currentView.goBack()
            return
        }
        if (mClickBackCount == 0) {
            //loadNativeAdsBack()
            showExitAppBS()
            mClickBackCount = 1
            return
        }

        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        menu?.let {
            adMenuItem = it.findItem(R.id.navigation_ads)
            shareMenuItem = it.findItem(R.id.navigation_share)
//            it.findItem(R.id.navigation_ads).isVisible = false
            it.findItem(R.id.navigation_desktop_site).isVisible = false
            it.findItem(R.id.navigation_share_with_friends).isVisible = false
//            it.findItem(R.id.navigation_share).isEnabled = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val currentView = tabsManager.currentTab
        var currentUrl = currentView?.url ?: ""

//        if (mainAdapter.getFragment(binding.viewPager.currentItem) is HomeFragment) {
//            currentUrl =
//                (mainAdapter.getFragment(binding.viewPager.currentItem) as HomeFragment).getCurrentPage()
//        }
        when (item.itemId) {
            R.id.navigation_new_tab -> {
                newTab(homePageInitializer, true)
                return true
            }
            R.id.navigation_share -> {
                if (!UrlUtils.isSpecialUrl(currentUrl)) {
                    ShareUtils.shareTextUrl(currentUrl, this)
                }
            }
            R.id.navigation_history -> {
                HistoryFragment().show(
                    supportFragmentManager,
                    HistoryFragment::class.java.simpleName
                )
            }
            R.id.navigation_copy_link -> {
                if (!UrlUtils.isSpecialUrl(currentUrl)) {
                    clipboardManager.copyToClipboard(currentUrl)
                }
            }
            R.id.navigation_bookmarks -> {
                openBookmarks()
            }
            R.id.navigation_add_bookmark -> {
                if (!UrlUtils.isSpecialUrl(currentUrl)) {
                    addBookmark(currentView?.title ?: "Homepage", currentUrl)
                }
            }
            R.id.navigation_desktop_site -> {
                snackbar(R.string.txt_menu_desktop_site)
            }
            R.id.navigation_share_with_friends -> {
                snackbar(R.string.txt_menu_share_with_friends)
            }
            R.id.navigation_setting -> {
                startActivity(
                    IntentActivity.getIntent(this, SettingFragment::class.java)
                )
            }
            R.id.navigation_ads -> {
                //no-op
                showAdsEffect()
            }
        }
        return true
    }

    private fun notifyTabViewAdded() {
        Log.e("ttt", "Notify Tab Added")
        tabsView?.tabAdded()
    }

    /**
     * helper function that opens the bookmark drawer
     */
    private fun openBookmarks() {
        if (binding.drawerLayout.isDrawerOpen(binding.rightDrawer)) {
            binding.drawerLayout.closeDrawers()
        }
        binding.drawerLayout.openDrawer(binding.rightDrawer)
    }

    // By using a manager, adds a bookmark and notifies third parties about that
    @SuppressLint("CheckResult")
    private fun addBookmark(title: String, url: String) {
        bookmarkRepository.addBookmarkIfNotExists(
            Bookmark.Entry(
                url,
                title,
                0,
                Bookmark.Folder.Root
            )
        )
            .subscribeOn(databaseScheduler)
            .observeOn(mainScheduler)
            .subscribe { boolean ->
                if (boolean) {
                    suggestionsAdapter?.refreshBookmarks()
                    bookmarksView?.handleUpdatedUrl(url)
                    toast(R.string.message_bookmark_added)
                }
            }
    }

    private fun addItemToHistory(title: String?, url: String) {
        if (UrlUtils.isSpecialUrl(url)) {
            return
        }

        historyRepository.visitHistoryEntry(url, title)
            .subscribeOn(databaseScheduler)
            .subscribe()
    }

    fun loadUrlInCurrentView(url: String) {
        updateOptionState()
        tabsManager.currentTab?.loadUrl(url)
    }

    override fun getUiColor(): Int {
        return Color.BLACK
    }

    override fun getUseDarkTheme(): Boolean {
        return false
    }

    override fun getTabModel() = tabsManager

    override fun changeToolbarBackground(favicon: Bitmap, tabBackground: Drawable?) {
        //TODO("Not yet implemented")
    }

    override fun updateUrl(url: String?, isLoading: Boolean) {
        if (url == null || searchView?.hasFocus() != false) {
            return
        }
        val currentTab = tabsManager.currentTab
        bookmarksView?.handleUpdatedUrl(url)

        val currentTitle = currentTab?.title

        searchView?.setText(searchBoxModel.getDisplayContent(url, currentTitle, isLoading))
    }

    override fun updateProgress(progress: Int) {
        Log.e("ttt", "updateProgress: $progress")
        setIsLoading(progress < 100)
        binding.tb.progressView.progress = progress.toFloat()
        if (progress < 100) {
            binding.tb.progressView.visibility = View.VISIBLE
        } else {
            binding.tb.progressView.visibility = View.GONE
        }
    }

    override fun updateHistory(title: String?, url: String) {
        addItemToHistory(title, url)
    }

    override fun openFileChooser(uploadMsg: ValueCallback<Uri>) {
        //TODO("Not yet implemented")
    }

    override fun showFileChooser(filePathCallback: ValueCallback<Array<Uri>>) {
        //TODO("Not yet implemented")
    }

    override fun onShowCustomView(view: View, callback: WebChromeClient.CustomViewCallback) {
        originalOrientation = requestedOrientation
        val requestedOrientation = originalOrientation
        onShowCustomView(view, callback, requestedOrientation)
    }

    override fun onShowCustomView(
        view: View,
        callback: WebChromeClient.CustomViewCallback,
        requestedOrientation: Int
    ) {
    }

    override fun onHideCustomView() {
        //TODO("Not yet implemented")
    }

    override fun onCreateWindow(resultMsg: Message) {
        newTab(ResultMessageInitializer(resultMsg), true)
        if (tabsManager.allTabs.size <= 10) {
            Handler().postDelayed(Runnable {
                val url = tabsManager.currentTab?.url
                if (!TextUtils.isEmpty(url)) {
                    deleteTab(tabsManager.indexOfCurrentTab())
                    url?.let { handleNewTab(LightningDialogBuilder.NewTab.FOREGROUND, it) }
                }
            }, 100)
        }
    }

    override fun onCloseWindow(tab: LightningView) {
        deleteTab(tabsManager.positionOf(tab))
    }

    override fun hideActionBar() {
        if (isFullScreen) {
            val height = binding.tb.toolbarLayout.height
            if (binding.tb.toolbarLayout.translationY > -0.01f) {
                val hideAnimation = object : Animation() {
                    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                        val trans = interpolatedTime * height
                        binding.tb.toolbarLayout.translationY = -trans
                        setWebViewTranslation(height - trans)
                    }
                }
                hideAnimation.duration = 250
                hideAnimation.interpolator = BezierDecelerateInterpolator()
                binding.main.contentFrame.startAnimation(hideAnimation)
            }
        }
    }

    override fun showActionBar() {
        if (isFullScreen) {
            Log.e("ttt", "showActionBar")
            var height = binding.tb.toolbarLayout.height
            if (height == 0) {
                binding.tb.toolbarLayout.measure(
                    View.MeasureSpec.UNSPECIFIED,
                    View.MeasureSpec.UNSPECIFIED
                )
                height = binding.tb.toolbarLayout.measuredHeight
            }

            val totalHeight = height
            if (binding.tb.toolbarLayout.translationY < -(height - 0.01f)) {
                val show = object : Animation() {
                    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                        val trans = interpolatedTime * totalHeight
                        binding.tb.toolbarLayout.translationY = trans - totalHeight
                        setWebViewTranslation(trans)
                    }
                }
                show.duration = 250
                show.interpolator = BezierDecelerateInterpolator()
                binding.main.contentFrame.startAnimation(show)
            }
        }
        binding.tb.toolbarLayout.visibility = View.VISIBLE
    }

    override fun showCloseDialog(position: Int) {
        //TODO("Not yet implemented")
    }

    override fun newTabButtonClicked() {
        newTab(
            homePageInitializer,
            true
        )
    }

    private fun deleteBookmark(title: String, url: String) {
        bookmarkRepository.deleteBookmark(Bookmark.Entry(url, title, 0, Bookmark.Folder.Root))
            .subscribeOn(databaseScheduler)
            .observeOn(mainScheduler)
            .subscribe { boolean ->
                if (boolean) {
                    suggestionsAdapter?.refreshBookmarks()
                    bookmarksView?.handleUpdatedUrl(url)
                }
            }
    }

    private fun resetTabSelect() {
        tabsManager.allTabs.forEach { it.focus = false }
    }

    override fun tabCloseClicked(position: Int) {
        Log.e("ttt", "tabCloseClicked: $position")
        deleteTab(position)
        updateOptionState()
    }

    override fun tabClicked(position: Int) {
        showTab(position)
    }

    override fun bookmarkButtonClicked() {
        val currentTab = tabsManager.currentTab
        val url = currentTab?.url
        val title = currentTab?.title
        if (url == null || title == null) {
            return
        }

        if (!UrlUtils.isSpecialUrl(url)) {
            bookmarkRepository.isBookmark(url)
                .subscribeOn(databaseScheduler)
                .observeOn(mainScheduler)
                .subscribe { boolean ->
                    if (boolean) {
                        deleteBookmark(title, url)
                    } else {
                        addBookmark(title, url)
                    }
                }
        }
    }

    override fun historyItemClicked(url: String) {
        loadUrlInCurrentView(url)
    }

    override fun bookmarkItemClicked(entry: Bookmark.Entry) {
        loadUrlInCurrentView(entry.url)
        // keep any jank from happening when the drawer is closed after the URL starts to load
        mainHandler.postDelayed({ closeDrawers(null) }, 150)
    }

    override fun setForwardButtonEnabled(enabled: Boolean) {
        binding.navigator.navigationNext.isEnabled = enabled
    }

    override fun setBackButtonEnabled(enabled: Boolean) {
        binding.navigator.navigationBack.isEnabled = enabled
    }

    override fun tabChanged(tab: LightningView) {
        notifyTabViewChanged(tabsManager.indexOfTab(tab))
    }

    override fun onBackButtonPressed() {
        //TODO("Not yet implemented")
    }

    override fun onForwardButtonPressed() {
        val currentTab = tabsManager.currentTab
        if (currentTab?.canGoForward() == true) {
            currentTab.goForward()
            closeDrawers(null)
        }
    }

    override fun onHomeButtonPressed() {
//        tabsManager.currentTab?.loadHomePage()
//        searchView?.setText("")
//        searchHomeView?.setText("")
//        searchView?.clearFocus()
//        closeDrawers(null)
//        binding.fab.visibility = View.GONE
//        AppUtil.hideSoftKeyboard(searchView!!)
//        binding.tb.toolbarLayout.requestFocus()
        HistoryFragment().show(
            supportFragmentManager,
            HistoryFragment::class.java.simpleName
        )
    }

    override fun handleBookmarksChange() {
        bookmarksView?.reloadData()
    }

    override fun handleBookmarkDeleted(bookmark: Bookmark) {
        bookmarksView?.handleBookmarkDeleted(bookmark)
    }

    override fun handleDownloadDeleted() {
        //TODO("Not yet implemented")
    }

    override fun handleHistoryChange() {
        val f = supportFragmentManager.findFragmentByTag(HistoryFragment::class.java.simpleName)
        if (f is HistoryFragment) {
            f.clearHistoryEntry()
        }
    }

    override fun handleNewTab(newTabType: LightningDialogBuilder.NewTab, url: String) {
        val urlInitializer = UrlInitializer(url)
        when (newTabType) {
            LightningDialogBuilder.NewTab.FOREGROUND -> {
                newTab(urlInitializer, true)
                mainHandler.postDelayed({ closeDrawers(null) }, 150)
            }
            LightningDialogBuilder.NewTab.BACKGROUND -> {
                newTab(urlInitializer, false)
                mainHandler.postDelayed({ closeDrawers(null) }, 150)
            }
            LightningDialogBuilder.NewTab.INCOGNITO -> TODO()
        }
    }

    private fun closeDrawers(runnable: (() -> Unit)?) {
        if (!binding.drawerLayout.isDrawerOpen(binding.rightDrawer)
        ) {
            if (runnable != null) {
                runnable()
                return
            }
        }
        binding.drawerLayout.closeDrawers()

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) = Unit

            override fun onDrawerOpened(drawerView: View) = Unit

            override fun onDrawerClosed(drawerView: View) {
                runnable?.invoke()
                binding.drawerLayout.removeDrawerListener(this)
            }

            override fun onDrawerStateChanged(newState: Int) = Unit
        })
    }

    private inner class SearchListenerClass(val sv: SearchView) : View.OnKeyListener,
        TextView.OnEditorActionListener,
        View.OnFocusChangeListener,
        SearchView.PreFocusListener,
        SearchView.OnLongPressListener,
        TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

        override fun afterTextChanged(e: Editable) {
            e.getSpans(0, e.length, CharacterStyle::class.java).forEach(e::removeSpan)
            e.getSpans(0, e.length, ParagraphStyle::class.java).forEach(e::removeSpan)
        }

        override fun onKey(view: View, keyCode: Int, keyEvent: KeyEvent): Boolean {

            when (keyCode) {
                KeyEvent.KEYCODE_ENTER -> {
                    sv.let {
                        AppUtil.hideSoftKeyboard(it)
                        searchTheWeb(it.text.toString())
                    }

                    tabsManager.currentTab?.requestFocus()
                    return true
                }
                else -> {
                }
            }
            return false
        }

        override fun onEditorAction(arg0: TextView, actionId: Int, arg2: KeyEvent?): Boolean {
            if (actionId == EditorInfo.IME_ACTION_GO
                || actionId == EditorInfo.IME_ACTION_DONE
                || actionId == EditorInfo.IME_ACTION_NEXT
                || actionId == EditorInfo.IME_ACTION_SEND
                || actionId == EditorInfo.IME_ACTION_SEARCH
                || arg2?.action == KeyEvent.KEYCODE_ENTER
            ) {
                sv.let {
                    AppUtil.hideSoftKeyboard(it)
                    searchTheWeb(it.text.toString())
                    if (sv.id != R.id.search) {
                        sv.setText("")
                    }
                }

                tabsManager.currentTab?.requestFocus()
                return true
            }
            return false
        }

        override fun onFocusChange(v: View, hasFocus: Boolean) {
            val currentView = tabsManager.currentTab
            if (!hasFocus && currentView != null) {
                setIsLoading(currentView.progress < 100)
                updateUrl(currentView.url, false)
            } else if (hasFocus && currentView != null) {

                // Hack to make sure the text gets selected
                (v as SearchView).selectAll()
                if (sv.id == R.id.search) {
                    iconDrawable = clearIconDrawable
                    sv.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        clearIconDrawable,
                        null
                    )
                }
            }

            if (!hasFocus) {
                sv.let {
                    AppUtil.hideSoftKeyboard(it)
                }
            }
        }

        override fun onPreFocus() {
            Log.e("ttt", "onPreFocus: ")
            val currentView = tabsManager.currentTab ?: return
            val url = currentView.url
            if (!UrlUtils.isSpecialUrl(url)) {
                if (!sv.hasFocus()) {
                    sv.setText(url)
                }
            }
        }

        override fun onLongPress() {
            sv.let {
                AppUtil.showSoftKeyboard(it)
            }
        }
    }

    /**
     * This method lets the search bar know that the page is currently loading
     * and that it should display the stop icon to indicate to the user that
     * pressing it stops the page from loading
     */
    private fun setIsLoading(isLoading: Boolean) {
        if (searchView?.hasFocus() == false) {
            iconDrawable = if (isLoading) deleteIconDrawable else refreshIconDrawable
            searchView?.setCompoundDrawablesWithIntrinsicBounds(
                sslDrawable,
                null,
                iconDrawable,
                null
            )
        }
    }

    /**
     * searches the web for the query fixing any and all problems with the input
     * checks if it is a search, url, etc.
     */
    private fun searchTheWeb(query: String) {
        Log.e("ttt", "searchTheWeb query = $query")
        val currentTab = tabsManager.currentTab
        if (query.isEmpty()) {
            return
        }
        val searchUrl = "$searchText${UrlUtils.QUERY_PLACE_HOLDER}"
        if (currentTab != null) {
            currentTab.stopLoading()
            loadUrlInCurrentView(UrlUtils.smartUrlFilter(query.trim(), true, searchUrl))
        }
    }

    //switch tab
    private fun showTab(position: Int) {
        tabChanged(position)
        updateOptionState()
    }

    /**
     * Deletes the tab at the specified position.
     *
     * @param position the position at which to delete the tab.
     */
    fun deleteTab(position: Int) {
        Log.d("DELETE_TAB", "deleteeee")
        val tabToDelete = tabsManager.getTabAtPosition(position) ?: return
        //Todo
//        recentTabModel.addClosedTab(tabToDelete.saveState())

        val isShown = tabToDelete.isShown
        //Todo
//        val shouldClose = shouldClose && isShown && tabToDelete.isNewTab
        val currentTab = tabsManager.currentTab
        if (tabsManager.size() == 1) {
            if (isShown) {
                removeTabView()
            }
            val currentDeleted = tabsManager.deleteTab(position)
            if (currentDeleted) {
                tabChanged(tabsManager.indexOfCurrentTab())
            }
            newTab(
                UrlInitializer("file:///data/user/0/com.highsecure.videodownloader.debug/files/homepage.html"),
                true
            )
            return
        } else {
            if (isShown) {
                removeTabView()
            }
            val currentDeleted = tabsManager.deleteTab(position)
            if (currentDeleted) {
                tabChanged(tabsManager.indexOfCurrentTab())
            }
        }

        val afterTab = tabsManager.currentTab
        notifyTabViewRemoved(position)

        if (afterTab == null) {
            closeBrowser()
            return
        } else if (afterTab !== currentTab) {
            notifyTabViewChanged(tabsManager.indexOfCurrentTab())
        }

        updateTabNumber(tabsManager.size())

        Log.e("ttt", "...deleted tab")
    }

    private fun notifyTabViewRemoved(position: Int) {
        Log.e("ttt", "Notify Tab Removed: $position")
        tabsView?.tabRemoved(position)
    }


    @Subscribe
    fun hidePopup(hide: String) {
        updateOptionState()

    }

    @Subscribe
    fun onSuccessGetLink123(videos: List<VideoInfo>) {
        Log.e("ttt", "onSuccessGetLink123: ||| ${videos.size}")
        mVideoDetects = videos.take(5).distinctBy { v -> v.format }
    }


    @Subscribe
    fun onSuccessGetLink(media: Media) {
        Log.e("ttt", "media: ${media.video.title} ||| ${media.video.videoUrl}")
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun showBottomSheetDownloadVideos(videos: List<VideoInfo>?) {
        videos?.let {
            if (it.isNotEmpty()) {
                val downloadBottomSheet = InfoListDownloadBottomSheet.newInstance(
                    it[0].title ?: "Video_${System.currentTimeMillis()}",
                    it[0].duration ?: "00:00",
                    it[0].thumbnail,
                    ArrayList(it)
                )
                downloadBottomSheet.mListener = object :
                    InfoListDownloadBottomSheet.DownloadBottomSheetListener {
                    override fun onDownload(video: VideoInfo) {
                        video.isSelect = false
                        mVideoDownload = video
                        downloadBottomSheet.dismiss()
                        showInterstitial(100)
                        Log.e(
                            "ttt",
                            "handleDownloadVideoEvent: url = ${video.url}  --- title = ${video.title}${video.ext}"
                        )
                    }
                }
                downloadBottomSheet.show(supportFragmentManager, "aaa")
            }
        }
    }


    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }


    private fun showBlockList(domains: List<DomainAllow>) {
        val list = ArrayList(mainViewModel.listPages.value ?: listOf())
        if (list.isNotEmpty()) {
            list.removeLast()
        }
        val downloadBottomSheet = TipDownloadBottomSheet.newInstance(list)
        downloadBottomSheet.mListener = object : TipDownloadBottomSheet.TipDomainClickListener {
            override fun onClick(domain: DomainAllow) {
                loadUrlInCurrentView(domain.domain!!)
            }
        }
        downloadBottomSheet.show(supportFragmentManager, "TipDownloadBottomSheet")
    }

    private fun checkUrlToShowPopup() {
        Log.e("ttt", "checkUrlToShowPopup")
        tabsManager.currentTab?.url?.let {
            val domainPath = PrefUtils.getListPathDomain(this)
            var hasPath = false
            for (d in domainPath) {
                if (d != null && it.contains(d)) {
                    hasPath = true
                    break
                }
            }
            if (hasPath) {
                showBlockList(listOf())
            } else {
                showDetailFAB()
            }
        } ?: showDetailFAB()
    }

    private fun showDetailFAB() {
        val dialog =
            DetailButtonDownloadDialog.newInstance(tabsManager.currentTab?.url ?: "")
        dialog.dismissListener = object : DetailButtonDownloadDialog.DismissListener {
            override fun onDismissNoResource(url: String) {
                mainViewModel.addFeedback(
                    "com.highsecure.videodownloader",
                    url,
                    ErrorType.ERROR_BROWSER_VIDEOS.name,
                    autoPush = true
                )
            }
        }
        dialog.show(supportFragmentManager, "abc")
    }

    private var isShowAdsDialog = false
    private fun showAdsEffect() {
        if (!isShowAdsDialog) {
            isShowAdsDialog = true
            val adsDialog = AdsDialog()
            adsDialog.dismissListener = object : AdsDialog.DismissListener {
                override fun onDismiss() {
                    isShowAdsDialog = false
                }
            }
            adsDialog.show(supportFragmentManager, "effect")
        }
    }

    private fun showExitAppBS() {
        binding.screenBack.visibility = View.VISIBLE
        binding.screenBack.setOnClickListener {
            mClickBackCount = 0
            binding.screenBack.visibility = View.GONE
        }

//        mExitAppBottomSheet = ExitAppBottomSheet()
//        mExitAppBottomSheet?.mListener = object : ExitAppBottomSheet.OnDismissListener {
//            override fun onDismiss() {
////                if(mExitAppBottomSheet?.isVisible == true) {
//                    onBackPressed()
////                } else {
////                    mClickBackCount = 0
////                }
//            }
//        }
//        mExitAppBottomSheet?.show(supportFragmentManager, "ExitAppBottomSheet")
    }


    private fun loadInterstitialAd() {
//        if (!BuildConfig.DEBUG) {
        val adRequest = AdRequest.Builder().build()
//            if (adRequest.isTestDevice(context)) {
//                Log.e("ttt", "loadInterstitialAd: isTestDevice")
//            }
        InterstitialAd.load(this, Constants.ADS_INTERSTITIAL_ID, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    mInterstitialAd = interstitialAd
                    Log.d("ttt", "mloadInterstitialAds onAdLoaded")
                    interstitialAd.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null
                                Log.w("ttt", "The ads was dismissed.")
                                binding.tb.toolbarLayout.visibility = View.VISIBLE
                                binding.container.visibility = View.VISIBLE
                                if (isShowAd) {
                                    openScreen(mTypeAds)
                                }
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                // Called when fullscreen content failed to show.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null
                                Log.w("TAG", "The ads failed to show.")
                            }

                            override fun onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                Log.w("ttt", "The ads was shown.")
                                binding.tb.toolbarLayout.visibility = View.GONE
                                binding.container.visibility = View.GONE
                                isShowAd = true
                            }
                        }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    Log.w("ttt", loadAdError.message)
                    mInterstitialAd = null
                    if (isShowAd) {
                        openScreen(mTypeAds)
                    }
                }
            })
//        }
    }

    private fun showInterstitial(type: Int) {
        Log.e("ttt", "showInterstitial")
        mTypeAds = type
        if (mInterstitialAd != null) {
            mInterstitialAd!!.show(this)
        } else {
            openScreen(type)
        }
    }

    private fun openScreen(type: Int) {
        Log.e("ttt", "openSreen: " + type)
        when (type) {
            0 -> openDownload()
            1 -> openTab()
            2 -> onHomeButtonPressed()
            100 -> download()
        }
    }

    private fun download() {
        Log.e("ttt", "start download")
        requestPermissionLauncherWriteStorage.launch(perms)
//        DownloadService.startDownload(
//            this@MainActivity,
//            DownloadItem("https://www.dailymotion.com/video/x8abj40"))
    }

    private fun openDownload() {
        startActivity(
            IntentActivity.getIntent(this, DownloadFragment::class.java)
        )
    }

    private fun openTab() {
        if (SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
            mLastClickTime = SystemClock.elapsedRealtime()
            (tabsView as TabsFragment).show(
                supportFragmentManager,
                TabsFragment::class.java.simpleName
            )
        }
    }

    private fun loadNativeAdsBack() {
        val adLoader = AdLoader.Builder(this, Constants.ADS_NATIVE_ID)
            .forNativeAd { ad: NativeAd ->
                // Show the ad.
                val styles = NativeTemplateStyle.Builder().build()
                binding.template2.setStyles(styles)
                binding.template2.setNativeAd(ad)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    binding.template2.visibility = View.GONE
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    // Methods in the NativeAdOptions.Builder class can be
                    // used here to specify individual options settings.
                    .build()
            )
            .build()

        adLoader.loadAds(AdRequest.Builder().build(), 3)
    }


    private fun checkSpace(downloadModel: VideoInfo) {
        Log.e("ttt", "Check space")
        val fileName = downloadModel.title
        Log.e("ttt", "Filename: " + fileName)
        var cutTitle = "" + fileName

        if (cutTitle == null || cutTitle.isEmpty()) {
            cutTitle = "Video_" + System.currentTimeMillis()
        } else if (downloadModel.url!!.contains("naverrmc")) {
            cutTitle = "Vlive_" + System.currentTimeMillis()
        } else {
            val characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]"
            cutTitle = cutTitle.replace(characterFilter.toRegex(), "")
            cutTitle = cutTitle.replace("['+.^:,#\"\\\\/]".toRegex(), "-")
            cutTitle = cutTitle.replace(" ", "-").replace("!", "").replace(":", "")
            if (cutTitle.length > 30) {
                cutTitle = cutTitle.substring(0, 30)
            }
        }

        Log.e("ttt", "Filename CutTile: " + cutTitle)
        PrefUtils.putString(this, downloadModel.url, cutTitle);

        val fileStartDownload = File(Constants.getExternalFile(), cutTitle + "11001001")
        val fileEndDownload = File(Constants.getExternalFile(), cutTitle + ".mp4")

        if (fileStartDownload.exists() || fileEndDownload.exists()) {
            CustomToast.makeText(
                this,
                getString(R.string.file_downloaded),
                CustomToast.SHORT,
                CustomToast.WARNING
            ).show()
        } else {
            if (Utils.getAllFile(this).size > 1) {
                val sizeVideo = (downloadModel.fileSize / 1048576).toLong()
                if (Utils.getAvailableSpaceInMB(this@WebActivity) <= 50) {
                    if (Utils.getAvailableSpaceInMB(this@WebActivity) - sizeVideo > 0) {
//                        DownloadService.startDownload(
//                            this@MainActivity,
//                            DownloadItem(downloadModel.url, cutTitle)
//                        )
                    } else {
                        CustomToast.makeText(
                            this,
                            getString(R.string.space_low),
                            CustomToast.SHORT,
                            CustomToast.WARNING
                        ).show()

                    }
                } else if (Utils.getAvailableSpaceInMB(this@WebActivity) <= sizeVideo) {
                    CustomToast.makeText(
                        this,
                        getString(R.string.space_full),
                        CustomToast.SHORT,
                        CustomToast.WARNING
                    ).show()

                } else if (Utils.getAvailableSpaceInMB(this@WebActivity) - sizeVideo > 0) {
//                    DownloadService.startDownload(
//                        this@MainActivity,
//                        DownloadItem(downloadModel.url, cutTitle)
//                    )
                }
            } else {
                if (PrefUtils.getExternalFilesDirName(this).contains(packageName)) {
                    CustomToast.makeText(
                        this,
                        getString(R.string.card_change),
                        CustomToast.SHORT,
                        CustomToast.WARNING
                    ).show()

                } else {
                    val sizeVideo = (downloadModel.fileSize / 1048576).toLong()
                    if (Utils.getAvailableSpaceInMB(this@WebActivity) <= 50) {
                        if (Utils.getAvailableSpaceInMB(this@WebActivity) - sizeVideo > 0) {
//                            DownloadService.startDownload(
//                                this@MainActivity,
//                                DownloadItem(downloadModel.url, cutTitle)
//                            )
                        } else {
                            CustomToast.makeText(
                                this,
                                getString(R.string.space_low),
                                CustomToast.SHORT,
                                CustomToast.WARNING
                            ).show()

                        }
                    } else if (Utils.getAvailableSpaceInMB(this@WebActivity) <= sizeVideo) {
                        CustomToast.makeText(
                            this,
                            getString(R.string.space_full),
                            CustomToast.SHORT,
                            CustomToast.WARNING
                        ).show()
                    } else if (Utils.getAvailableSpaceInMB(this@WebActivity) - sizeVideo > 0) {
//                        DownloadService.startDownload(
//                            this@MainActivity,
//                            DownloadItem(downloadModel.url, cutTitle)
//                        )
                    }
                }
            }
        }
    }



    private fun addNewShortcut() {
        val dialog = Dialog(this, R.style.MyDialog)
        val dialogBinding = DialogAddNewShortcutBinding.inflate(layoutInflater)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(dialogBinding.root)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnSave.setOnClickListener {
            val websiteName = dialogBinding.ctWebsiteName.editText?.text
            var websiteUrl = dialogBinding.ctUrl.editText?.text.toString()
            if (websiteName != null) {
                if (websiteName.isNotEmpty() && websiteUrl.isNotEmpty()) {
                    if (!websiteUrl.contains("https://") || !websiteUrl.contains("http://")) {
                        websiteUrl = "https://${websiteUrl}"
                    }
                    webViewModel.insertShortcut(
                        Shortcut(
                            null,
                            websiteName.toString(),
                            websiteUrl,
                            true
                        )
                    )
                    FancyToast.makeText(
                        this,
                        "Done!",
                        FancyToast.LENGTH_SHORT,
                        FancyToast.SUCCESS,
                        false
                    ).show().run {
                        dialog.dismiss()
                    }
                } else {
                    FancyToast.makeText(
                        this,
                        "Vui lòng không để trống",
                        FancyToast.LENGTH_SHORT,
                        FancyToast.WARNING,
                        false
                    ).show()
                }
            }
        }
    }
}
