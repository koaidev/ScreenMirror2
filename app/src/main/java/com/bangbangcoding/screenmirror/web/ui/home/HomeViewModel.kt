package com.bangbangcoding.screenmirror.web.ui.home

import android.annotation.SuppressLint
import android.util.Log
import android.util.Patterns
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangbangcoding.screenmirror.BuildConfig
import com.bangbangcoding.screenmirror.web.data.history.HistoryRepository
import com.bangbangcoding.screenmirror.web.data.local.model.Suggestion
import com.bangbangcoding.screenmirror.web.data.local.room.entity.PageInfo
import com.bangbangcoding.screenmirror.web.data.remote.request.FeedbackRequest
import com.bangbangcoding.screenmirror.web.data.repository.ConfigRepository
import com.bangbangcoding.screenmirror.web.data.repository.FeedbackRepository
import com.bangbangcoding.screenmirror.web.data.repository.TopPagesRepository
import com.bangbangcoding.screenmirror.web.data.repository.VideoRepository
import com.bangbangcoding.screenmirror.web.di.module.DatabaseScheduler
import com.bangbangcoding.screenmirror.web.di.module.MainScheduler
import com.bangbangcoding.screenmirror.web.di.module.NetworkScheduler
import com.bangbangcoding.screenmirror.web.ui.base.BaseViewModel
import com.bangbangcoding.screenmirror.web.ui.find_video.ConcreteVideoContentSearch
import com.bangbangcoding.screenmirror.web.ui.model.apis_models.VideoInfo
import com.bangbangcoding.screenmirror.web.ui.search.SearchEngineProvider
import com.bangbangcoding.screenmirror.web.utils.Constants
import com.bangbangcoding.screenmirror.web.utils.SingleLiveEvent
import com.bangbangcoding.screenmirror.web.utils.add
import com.bangbangcoding.screenmirror.web.utils.scheduler.BaseSchedulers
import com.bangbangcoding.screenmirror.web.utils.toSHA256String
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val topPagesRepository: TopPagesRepository,
    private val configRepository: ConfigRepository,
    private val videoRepository: VideoRepository,
    private val historyRepository: HistoryRepository,
    private val baseSchedulers: BaseSchedulers,
    private val searchEngineProvider: SearchEngineProvider,
    @DatabaseScheduler val databaseScheduler: Scheduler,
    @NetworkScheduler val networkScheduler: Scheduler,
    @MainScheduler val mainScheduler: Scheduler,
    private val feedbackRepository: FeedbackRepository,
) : BaseViewModel() {

    companion object {
        const val SEARCH_URL = "https://www.google.com/search?q=%s"
    }

    private val _loadScript = MutableLiveData<Boolean>().apply {
        value = false
    }
    val loadScript: LiveData<Boolean> = _loadScript

    lateinit var contentSearch: ConcreteVideoContentSearch

    lateinit var publishSubject: PublishSubject<String>
    var currentPage: String = ""

    private val _textInput = MutableLiveData<String>().apply {
        value = ""
    }
    val textInput: LiveData<String> = _textInput

    private val _isShowPage = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isShowPage: LiveData<Boolean> = _isShowPage

    private val _isExpandedAppbar = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isExpandedAppbar: LiveData<Boolean> = _isExpandedAppbar

    private val _isFocus = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isFocus: LiveData<Boolean> = _isFocus

    private val _isShowFabBtn = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isShowFabBtn: LiveData<Boolean> = _isShowFabBtn

    private val _isLoadingVideoInfo = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isLoadingVideoInfo: LiveData<Boolean> = _isLoadingVideoInfo

    private val _isShowProgress = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isShowProgress: LiveData<Boolean> = _isShowProgress

    private val _pageUrl = MutableLiveData<String>().apply {
        value = ""
    }
    val pageUrl: LiveData<String> = _pageUrl

    private val _listPages = MutableLiveData<List<PageInfo>>().apply {
        value = listOf()
    }
    val listPages: LiveData<List<PageInfo>> = _listPages

    private val _listSuggestions = MutableLiveData<List<Suggestion>>().apply {
        value = listOf()
    }
    val listSuggestions: LiveData<List<Suggestion>> = _listSuggestions

    private val _progress = MutableLiveData<Int>().apply {
        value = 0
    }
    val progress: LiveData<Int> = _progress

    private val _startDetect = MutableLiveData<String>().apply {
        value = ""
    }
    val startDetect: LiveData<String> = _startDetect


    private val _finishLoadPage = MutableLiveData<Boolean>().apply {
        value = false
    }
    val finishLoadPage: LiveData<Boolean> = _finishLoadPage

    val changeFocusEvent = SingleLiveEvent<Boolean>()
    val showDownloadDialogEvent = SingleLiveEvent<VideoInfo>()

    var mVideoInfo : VideoInfo? = null

    @VisibleForTesting
    internal lateinit var  compositeDisposable: CompositeDisposable

    fun setConcreteVideoContentSearch(contentSearch: ConcreteVideoContentSearch) {
        this.contentSearch = contentSearch
    }

    override fun start() {
        compositeDisposable = CompositeDisposable()
        publishSubject = PublishSubject.create()
        getTopPages()
    }

    override fun stop() {
        compositeDisposable.clear()
    }

    fun loadPage(input: String, pattern: Pattern = Patterns.WEB_URL) {
        if (input.isNotEmpty()) {
            _isShowPage.value = true
            changeFocus(false)

            if (input.startsWith("http://") || input.startsWith("https://")) {
                _pageUrl.value = input
            } else if (pattern.matcher(input).matches()) {
                _pageUrl.value = "http://$input"
                _textInput.value = "http://$input"
            } else {
                val searchUrl =
                    "${searchEngineProvider.provideSearchEngine().queryUrl}${input}"
//                _pageUrl.value = String.format(SEARCH_URL, input)
//                _textInput.value = String.format(SEARCH_URL, input)
                _pageUrl.value = searchUrl
                _textInput.value = searchUrl
            }
        }
    }

    fun changeFocus(isFocus: Boolean) {
        _isFocus.value = isFocus
        changeFocusEvent.value = isFocus
    }

    fun startPage(url: String) {
//        _textInput.value = url
        _isShowPage.value = true
        _isShowProgress.value = true
        _isExpandedAppbar.value = true
//        verifyLinkStatus(url)
    }

    fun loadResource(url: String) {
        Log.e("ttt", "urlResource: $url // ${_textInput.value}")
        if (url != _textInput.value) {
            _textInput.value = url
            _startDetect.value = url
        }
//        if (url.contains("facebook.com") || url.contains("fb.watch")) {
//            _startDetect.value = url
//        }
        loadScriptFB(url)
    }

    fun finishPage(url: String) {
        _textInput.value = url
        _isShowProgress.value = false
//        verifyLinkStatus(url)
//        getLinkResource(url)
        _finishLoadPage.value = true
    }

    private fun loadScriptFB(url: String) {
        if (url.contains("facebook.com")) {
            _loadScript.value = true
        }

    }

    @SuppressLint("CheckResult")
    @VisibleForTesting
    internal fun verifyLinkStatus(url: String) {
        configRepository.getSupportedPages()
            .flatMap { pages ->
                Flowable.fromIterable(pages)
                    .filter { page ->
                        url.matches(page.pattern.toRegex()) || url.contains(page.pattern)
                    }.toList().toFlowable()
            }
            .subscribeOn(baseSchedulers.io)
            .observeOn(baseSchedulers.mainThread)
            .firstOrError()
            .doOnSubscribe { compositeDisposable.add(it) }
            .subscribe({ pages ->
                _isShowFabBtn.value = pages.isNotEmpty()
            }, { error ->
                error.printStackTrace()
                _isShowFabBtn.value = false
            })
    }

    fun showSuggestions() {
        getListSuggestions()
            .subscribeOn(baseSchedulers.io)
            .observeOn(baseSchedulers.mainThread)
            .firstOrError()
            .doOnSubscribe { compositeDisposable.add(it) }
            .subscribe({ list ->
//                with(listSuggestions) {
//                    clear()
//                    addAll(list)
//                }
            }, { error ->
                error.printStackTrace()
            }).add(this)
    }

    private fun getListSuggestions(): Flowable<List<Suggestion>> {
        return Flowable.combineLatest(
            publishSubject.debounce(300, TimeUnit.MILLISECONDS)
                .toFlowable(BackpressureStrategy.LATEST),
            configRepository.getSupportedPages()
        ) { input, supportedPages ->
            val listSuggestions = mutableListOf<Suggestion>()
            supportedPages.filter { page -> page.name.contains(input) }.map {
                listSuggestions.add(Suggestion(url = it.name))
            }
            listSuggestions
        }
    }

    fun getHistoryEntry(query : String) {
        historyRepository.findHistoryEntriesContaining(query)
            .subscribeOn(databaseScheduler)
            .observeOn(mainScheduler)
            .subscribe { list ->
                _listSuggestions.value = list.map { Suggestion(it.url, it.title) }
            }.add(this)
    }

    fun getVideoInfo() {
        textInput.value?.let { url ->
            videoRepository.getVideoInfo(url)
                .doOnSubscribe { _isLoadingVideoInfo.postValue(true) }
                .doAfterTerminate { _isLoadingVideoInfo.postValue(false) }
                .subscribeOn(baseSchedulers.io)
                .observeOn(baseSchedulers.mainThread)
                .firstOrError()
                .doOnSubscribe { compositeDisposable.add(it) }
                .subscribe({ videoInfo ->
//                    showDownloadDialogEvent.value = videoInfo
                }, { error ->
                    error.printStackTrace()
                })
        }
    }

    private fun getTopPages() {
        topPagesRepository.getTopPages()
            .subscribeOn(baseSchedulers.io)
            .observeOn(baseSchedulers.mainThread)
            .firstOrError()
            .doOnSubscribe { compositeDisposable.add(it) }
            .subscribe({ list ->
//                _listPages.value = list
            }, { error ->
                error.printStackTrace()
            }).add(this)
    }

    fun setTextInput(s: String) {
        _textInput.value = s
    }

    fun setPageUrl(url: String) {
        _pageUrl.value = url
    }

    fun setProgress(newProgress: Int) {
        _progress.value = newProgress
    }

    fun setIsShowPage(b: Boolean) {
        _isShowPage.value = b
    }

    fun setDataDownload(
        size: String?, type: String?, link: String?, name: String?, page: String?,
        chunked: Boolean, website: String?
    ) {
        var type1 = ".mp4"
        type?.let {
            if (!it.contains(".")) {
                type1 = ".$it"
            }
        }
        mVideoInfo = VideoInfo().apply {
            this.url = link ?: ""
            this.fileSize = size?.toLong() ?: 0L
            this.title = name
            this.ext = type1
        }
    }

    fun downloadByManual(url: String) {
        if (url.contains("facebook")
            ||url.contains("fb.watch")
            ||url.contains("tiktok")
        ) {
            showDownloadDialogEvent.value = mVideoInfo
        }
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
}