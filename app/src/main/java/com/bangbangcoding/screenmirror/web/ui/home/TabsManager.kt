package com.bangbangcoding.screenmirror.web.ui.home

import android.app.Activity
import android.app.Application
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.webkit.URLUtil
import com.bangbangcoding.screenmirror.web.di.ActivityScoped
import com.bangbangcoding.screenmirror.web.di.module.DatabaseScheduler
import com.bangbangcoding.screenmirror.web.di.module.DiskScheduler
import com.bangbangcoding.screenmirror.web.di.module.MainScheduler
import com.bangbangcoding.screenmirror.web.logger.Logger
import com.bangbangcoding.screenmirror.web.ui.preference.UserPreferences
import com.bangbangcoding.screenmirror.web.ui.search.SearchEngineProvider
import com.bangbangcoding.screenmirror.web.ui.widget.BookmarkPageInitializer
import com.bangbangcoding.screenmirror.web.ui.widget.BundleInitializer
import com.bangbangcoding.screenmirror.web.ui.widget.DownloadPageInitializer
import com.bangbangcoding.screenmirror.web.ui.widget.HistoryPageInitializer
import com.bangbangcoding.screenmirror.web.ui.widget.HomePageInitializer
import com.bangbangcoding.screenmirror.web.ui.widget.LightningView
import com.bangbangcoding.screenmirror.web.ui.widget.PermissionInitializer
import com.bangbangcoding.screenmirror.web.ui.widget.TabInitializer
import com.bangbangcoding.screenmirror.web.ui.widget.UrlInitializer
import com.bangbangcoding.screenmirror.web.utils.FileUtils
import com.bangbangcoding.screenmirror.web.utils.Option
import com.bangbangcoding.screenmirror.web.utils.UrlUtils
import com.bangbangcoding.screenmirror.web.utils.value
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject

/**
 * A manager singleton that holds all the [LightningView] and tracks the current tab. It handles
 * creation, deletion, restoration, state saving, and switching of tabs.
 */
@ActivityScoped
class TabsManager @Inject constructor(
    private val application: Application,
    private val searchEngineProvider: SearchEngineProvider,
    @DatabaseScheduler private val databaseScheduler: Scheduler,
    @DiskScheduler private val diskScheduler: Scheduler,
    @MainScheduler private val mainScheduler: Scheduler,
    private val homePageInitializer: HomePageInitializer,
    private val bookmarkPageInitializer: BookmarkPageInitializer,
    private val historyPageInitializer: HistoryPageInitializer,
    private val downloadPageInitializer: DownloadPageInitializer,
    private val logger: Logger,
    private val userPrefs: UserPreferences
) {

    private val tabList = arrayListOf<LightningView>()

    /**
     * Return the current [LightningView] or null if no current tab has been set.
     *
     * @return a [LightningView] or null if there is no current tab.
     */
    var currentTab: LightningView? = null
        private set

    private var tabNumberListeners = emptySet<(Int) -> Unit>()

    private var isInitialized = false
    private var postInitializationWorkList = emptyList<() -> Unit>()

    fun addTabNumberChangedListener(listener: ((Int) -> Unit)) {
        tabNumberListeners += listener
    }

    fun cancelPendingWork() {
        postInitializationWorkList = emptyList()
    }

    fun doAfterInitialization(runnable: () -> Unit) {
        if (isInitialized) {
            runnable()
        } else {
            postInitializationWorkList += runnable
        }
    }

    private fun finishInitialization() {
        isInitialized = true
        for (runnable in postInitializationWorkList) {
            runnable()
        }
    }

    /**
     * Initialize the state of the [TabsManager] based on previous state of the browser and with the
     * new provided [intent] and emit the last tab that should be displayed. By default operates on
     * a background scheduler and emits on the foreground scheduler.
     */
    fun initializeTabs(
        activity: Activity,
        intent: Intent?,
        incognito: Boolean
    ): Single<LightningView> =
        Single
            .just(
                Option.fromNullable(
                    if (intent?.action == Intent.ACTION_WEB_SEARCH) {
                        extractSearchFromIntent(intent)
                    } else {
                        intent?.dataString
                    }
                )
            )
            .doOnSuccess { shutdown() }
            .subscribeOn(mainScheduler)
            .flatMapObservable {
                return@flatMapObservable if (incognito) {
                    initializeIncognitoMode(it.value())
                } else {
                    initializeRegularMode(it.value(), activity)
                }
            }
            .subscribeOn(databaseScheduler)
            .observeOn(mainScheduler)
            .map { newTab(activity, it, incognito, userPrefs) }
            .lastOrError()
            .doAfterSuccess { finishInitialization() }

    /**
     * Returns an [Observable] that emits the [TabInitializer] for incognito mode.
     */
    private fun initializeIncognitoMode(initialUrl: String?): Observable<TabInitializer> =
        Observable.fromCallable {
            return@fromCallable initialUrl?.let(::UrlInitializer) ?: homePageInitializer
        }

    /**
     * Returns an [Observable] that emits the [TabInitializer] for normal operation mode.
     */
    private fun initializeRegularMode(
        initialUrl: String?,
        activity: Activity
    ): Observable<TabInitializer> =
        restorePreviousTabs()
            .concatWith(Maybe.fromCallable<TabInitializer> {
                return@fromCallable initialUrl?.let {
                    if (URLUtil.isFileUrl(it)) {
                        PermissionInitializer(it, activity, homePageInitializer)
                    } else {
                        UrlInitializer(it)
                    }
                }
            })
            .defaultIfEmpty(homePageInitializer)

    /**
     * Returns the URL for a search [Intent]. If the query is empty, then a null URL will be
     * returned.
     */
    fun extractSearchFromIntent(intent: Intent): String? {
        val query = intent.getStringExtra(SearchManager.QUERY)
        val searchUrl =
            "${searchEngineProvider.provideSearchEngine().queryUrl}${UrlUtils.QUERY_PLACE_HOLDER}"

        return if (query?.isNotBlank() == true) {
            UrlUtils.smartUrlFilter(query, true, searchUrl)
        } else {
            null
        }
    }

    /**
     * Returns an observable that emits the [TabInitializer] for each previously opened tab as
     * saved on disk. Can potentially be empty.
     */
    private fun restorePreviousTabs(): Observable<TabInitializer> = readSavedStateFromDisk()
        ?.map { bundle ->
            return@map bundle.getString(URL_KEY)?.let { url ->
                when {
                    UrlUtils.isBookmarkUrl(url) -> bookmarkPageInitializer
                    UrlUtils.isDownloadsUrl(url) -> downloadPageInitializer
                    UrlUtils.isStartPageUrl(url) -> homePageInitializer
                    UrlUtils.isHistoryUrl(url) -> historyPageInitializer
                    else -> homePageInitializer
                }
            } ?: BundleInitializer(bundle)
        }!!


    /**
     * Method used to resume all the tabs in the browser. This is necessary because we cannot pause
     * the WebView when the application is open currently due to a bug in the WebView, where calling
     * onResume doesn't consistently resume it.
     */
    fun resumeAll() {
        currentTab?.resumeTimers()
        for (tab in tabList) {
            tab.onResume()
            tab.initializePreferences()
        }
    }

    /**
     * Method used to pause all the tabs in the browser. This is necessary because we cannot pause
     * the WebView when the application is open currently due to a bug in the WebView, where calling
     * onResume doesn't consistently resume it.
     */
    fun pauseAll() {
        //currentTab?.pauseTimers()  //gọi hàm này sẽ dừng các màn hình, ko click được vào Ads
        currentTab?.onPause()
        tabList.forEach(LightningView::onPause)
    }

    /**
     * Return the tab at the given position in tabs list, or null if position is not in tabs list
     * range.
     *
     * @param position the index in tabs list
     * @return the corespondent [LightningView], or null if the index is invalid
     */
    fun getTabAtPosition(position: Int): LightningView? =
        if (position < 0 || position >= tabList.size) {
            null
        } else {
            tabList[position]
        }

    val allTabs: List<LightningView>
        get() = tabList

    /**
     * Shutdown the manager. This destroys all tabs and clears the references to those tabs. Current
     * tab is also released for garbage collection.
     */
    fun shutdown() {
        repeat(tabList.size) { deleteTab(0) }
        isInitialized = false
        currentTab = null
    }

    /**
     * The current number of tabs in the manager.
     *
     * @return the number of tabs in the list.
     */
    fun size(): Int = tabList.size

    /**
     * The index of the last tab in the manager.
     *
     * @return the last tab in the list or -1 if there are no tabs.
     */
    fun last(): Int = tabList.size - 1


    /**
     * The last tab in the tab manager.
     *
     * @return the last tab, or null if there are no tabs.
     */
    fun lastTab(): LightningView? = tabList.lastOrNull()

    /**
     * Create and return a new tab. The tab is automatically added to the tabs list.
     *
     * @param activity the activity needed to create the tab.
     * @param tabInitializer the initializer to run on the tab after it's been created.
     * @param isIncognito whether the tab is an incognito tab or not.
     * @return a valid initialized tab.
     */
    fun newTab(
        activity: Activity,
        tabInitializer: TabInitializer,
        isIncognito: Boolean,
        userPrefs: UserPreferences
    ): LightningView {
        logger.log(TAG, "New tab")
        val tab = LightningView(
            activity,
            tabInitializer,
            isIncognito,
            homePageInitializer,
            logger,
            userPrefs
        )

        tabList.add(tab)
        tabNumberListeners.forEach { it(size()) }
        return tab
    }

    /**
     * Removes a tab from the list and destroys the tab. If the tab removed is the current tab, the
     * reference to the current tab will be nullified.
     *
     * @param position The position of the tab to remove.
     */
    private fun removeTab(position: Int) {
        if (position >= tabList.size) {
            return
        }
//        val tab = (list as ArrayList).removeAt(position)
        val tab = tabList.removeAt(position)
        if (currentTab == tab) {
            currentTab = null
        }
        tab.onDestroy()
    }

    /**
     * Deletes a tab from the manager. If the tab being deleted is the current tab, this method will
     * switch the current tab to a new valid tab.
     *
     * @param position the position of the tab to delete.
     * @return returns true if the current tab was deleted, false otherwise.
     */
    fun deleteTab(position: Int): Boolean {
        logger.log(TAG, "Delete tab: $position")
        val currentTab = currentTab
        val current = positionOf(currentTab)

        if (current == position) {
            when {
                size() == 1 -> this.currentTab = null
                current < size() - 1 -> switchToTab(current + 1)
                else -> switchToTab(current - 1)
            }
        }

        removeTab(position)
        tabNumberListeners.forEach { it(size()) }
        return current == position
    }

    /**
     * Return the position of the given tab.
     *
     * @param tab the tab to look for.
     * @return the position of the tab or -1 if the tab is not in the list.
     */
    fun positionOf(tab: LightningView?): Int = tabList.indexOf(tab)

    /**
     * Saves the state of the current WebViews, to a bundle which is then stored in persistent
     * storage and can be unparceled.
     */
    fun saveState() {
        val outState = Bundle(ClassLoader.getSystemClassLoader())
        logger.log(TAG, "Saving tab state")
        tabList
            .filter { it.url.isNotBlank() }
            .withIndex()
            .forEach { (index, tab) ->
                if (!UrlUtils.isSpecialUrl(tab.url)) {
                    outState.putBundle(BUNDLE_KEY + index, tab.saveState())
                } else {
                    outState.putBundle(BUNDLE_KEY + index, Bundle().apply {
                        putString(URL_KEY, tab.url)
                    })
                }
            }
        FileUtils.writeBundleToStorage(application, outState, BUNDLE_STORAGE)
            .subscribeOn(diskScheduler)
            .subscribe()
        logger.log(TAG, "Saving tab state" + tabList.size)
    }

    /**
     * Use this method to clear the saved state if you do not wish it to be restored when the
     * browser next starts.
     */
    fun clearSavedState() = FileUtils.deleteBundleInStorage(application, BUNDLE_STORAGE)

    /**
     * Creates an [Observable] that emits the [Bundle] state stored for each previously opened tab
     * on disk. After the list of bundle [Bundle] is read off disk, the old state will be deleted.
     * Can potentially be empty.
     */
    private fun readSavedStateFromDisk(): Observable<Bundle?>? = Maybe
        .fromCallable { FileUtils.readBundleFromStorage(application, BUNDLE_STORAGE) }
        .flattenAsObservable { bundle ->
            bundle.keySet()
                .filter { it.startsWith(BUNDLE_KEY) }
                .map(bundle::getBundle)
        }
        .doOnNext { logger.log(TAG, "Restoring previous WebView state now") }

    /**
     * Returns the index of the current tab.
     *
     * @return Return the index of the current tab, or -1 if the current tab is null.
     */
    fun indexOfCurrentTab(): Int = tabList.indexOf(currentTab)

    /**
     * Returns the index of the tab.
     *
     * @return Return the index of the tab, or -1 if the tab isn't in the list.
     */
    fun indexOfTab(tab: LightningView): Int = tabList.indexOf(tab)

    /**
     * Returns the [LightningView] with the provided hash, or null if there is no tab with the hash.
     *
     * @param hashCode the hashcode.
     * @return the tab with an identical hash, or null.
     */
    fun getTabForHashCode(hashCode: Int): LightningView? =
        tabList.firstOrNull { lightningView -> lightningView.webView?.let { it.hashCode() == hashCode } == true }

    /**
     * Switch the current tab to the one at the given position. It returns the selected tab that has
     * been switched to.
     *
     * @return the selected tab or null if position is out of tabs range.
     */
    fun switchToTab(position: Int): LightningView? {
        logger.log(TAG, "switch to tab: $position")
        return if (position < 0 || position >= tabList.size) {
            logger.log(TAG, "Returning a null LightningView requested for position: $position")
            null
        } else {
            tabList[position].also {
                currentTab = it
            }
        }
    }

    companion object {

        private const val TAG = "TabsManager"

        private const val BUNDLE_KEY = "WEBVIEW_"
        private const val URL_KEY = "URL_KEY"
        private const val BUNDLE_STORAGE = "SAVED_TABS.parcel"
    }

}