package com.bangbangcoding.screenmirror.web.ui

import android.graphics.Bitmap
import android.util.Log
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangbangcoding.screenmirror.BuildConfig
import com.bangbangcoding.screenmirror.web.data.history.HistoryRepository
import com.bangbangcoding.screenmirror.web.data.local.model.DomainAllow
import com.bangbangcoding.screenmirror.web.data.remote.request.FeedbackRequest
import com.bangbangcoding.screenmirror.web.data.repository.ConfigRepository
import com.bangbangcoding.screenmirror.web.data.repository.FeedbackRepository
import com.bangbangcoding.screenmirror.web.data.repository.TopPagesRepository
import com.bangbangcoding.screenmirror.web.data.repository.VideoRepository
import com.bangbangcoding.screenmirror.web.di.module.DatabaseScheduler
import com.bangbangcoding.screenmirror.web.di.module.DiskScheduler
import com.bangbangcoding.screenmirror.web.di.module.MainScheduler
import com.bangbangcoding.screenmirror.web.di.module.NetworkScheduler
import com.bangbangcoding.screenmirror.web.favicon.FaviconModel
import com.bangbangcoding.screenmirror.web.ui.base.BaseViewModel
import com.bangbangcoding.screenmirror.web.ui.search.SearchEngineProvider
import com.bangbangcoding.screenmirror.web.ui.widget.LightningView
import com.bangbangcoding.screenmirror.web.utils.Constants
import com.bangbangcoding.screenmirror.web.utils.add
import com.bangbangcoding.screenmirror.web.utils.scheduler.BaseSchedulers
import com.bangbangcoding.screenmirror.web.utils.toSHA256String
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by Thao on 12/11/21.
 */

class MainViewModel @Inject constructor(
    private val topPagesRepository: TopPagesRepository,
    private val configRepository: ConfigRepository,
    private val videoRepository: VideoRepository,
    private val historyRepository: HistoryRepository,
    private val baseSchedulers: BaseSchedulers,
    private val searchEngineProvider: SearchEngineProvider,
    private val faviconModel: FaviconModel,
    @DatabaseScheduler val databaseScheduler: Scheduler,
    @NetworkScheduler val networkScheduler: Scheduler,
    @MainScheduler val mainScheduler: Scheduler,
    @DiskScheduler val diskScheduler: Scheduler,
    private val feedbackRepository: FeedbackRepository,
    ) : BaseViewModel() {

    override fun start() {
        compositeDisposable = CompositeDisposable()
        getTopPages()
        getBlockList()
    }

    override fun stop() {
        compositeDisposable.clear()
    }


    private val _listPages = MutableLiveData<List<DomainAllow>>().apply {
        value = listOf()
    }
    val listPages: LiveData<List<DomainAllow>> = _listPages

    private val _listPagesBlock = MutableLiveData<List<DomainAllow>>().apply {
        value = listOf()
    }
    val listPagesBlock: LiveData<List<DomainAllow>> = _listPagesBlock


    @VisibleForTesting
    internal lateinit var compositeDisposable: CompositeDisposable

    private fun getTopPages() {
        topPagesRepository.getTopPages()
            .subscribeOn(baseSchedulers.io)
            .observeOn(baseSchedulers.mainThread)
            .firstOrError()
            .doOnSubscribe { compositeDisposable.add(it) }
            .subscribe({ list ->
                Log.e("ttt", "domain: ${list.size}")
                _listPages.value = list
            }, { error ->
                error.printStackTrace()
            }).add(this)
    }

    private fun getBlockList() {
        topPagesRepository.getBlockList()
            .subscribeOn(baseSchedulers.io)
            .observeOn(baseSchedulers.mainThread)
            .firstOrError()
            .doOnSubscribe { compositeDisposable.add(it) }
            .subscribe({ list ->
                Log.e("ttt", "domainBlock: $list")
                _listPagesBlock.value = list
            }, { error ->
                error.printStackTrace()
            }).add(this)
    }

    fun addFeedback(appID: String, url: String, type: String, autoPush: Boolean = false) {
        val timestamp = System.currentTimeMillis() / 1000L
        val text = "$appID.$url.$type.$timestamp.${Constants.SECRET_KEY}"
        val checksum = text.toSHA256String()
        val request = FeedbackRequest(appID, url, type, timestamp, checksum, BuildConfig.VERSION_NAME, autoPush)
        Log.e("ttt", "addFeedback: $request")
        feedbackRepository.addFeedback(request)
            .subscribeOn(baseSchedulers.io)
            .observeOn(baseSchedulers.mainThread)
            .firstOrError()
//            .doOnSubscribe { compositeDisposable.add(it) }
            .subscribe({ data ->
                Log.e("ttt", "addFeedback: success $data")
            }, { error ->
                Log.e("ttt", "Error: fail ${error.message}")
                error.printStackTrace()
            }).add(this)
    }

    /**
     * Naive caching of the favicon according to the domain name of the URL
     *
     * @param icon the icon to cache
     */
    fun cacheScreenShot(v: View, lightningView: LightningView?, url: String?) {
        if (url == null || lightningView == null) {
            return
        }
//        val v: View = activity.window.decorView.rootView
        try {
            v.isDrawingCacheEnabled = true
            val bmp = Bitmap.createBitmap(v.drawingCache)
            v.isDrawingCacheEnabled = false
            lightningView.titleInfo.setScreenShot(bmp)

            faviconModel.cacheSSForUrl(bmp, url)
                .subscribeOn(diskScheduler)
                .subscribe()
        }catch (e:Exception){
            Log.e("MainViewModel","cacheScreenShot " + e.toString())
        }

    }
}