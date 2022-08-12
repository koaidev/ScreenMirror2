package com.bangbangcoding.screenmirror.web.ui.home

import android.util.Patterns
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangbangcoding.screenmirror.web.data.local.model.Suggestion
import com.bangbangcoding.screenmirror.web.data.local.room.entity.VideoInfo
import com.bangbangcoding.screenmirror.web.data.repository.ConfigRepository
import com.bangbangcoding.screenmirror.web.data.repository.TopPagesRepository
import com.bangbangcoding.screenmirror.web.data.repository.VideoRepository
import com.bangbangcoding.screenmirror.web.ui.base.BaseViewModel
import com.bangbangcoding.screenmirror.web.utils.SingleLiveEvent
import com.bangbangcoding.screenmirror.web.utils.scheduler.BaseSchedulers
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import javax.inject.Inject

class BrowserViewModel @Inject constructor(
    private val topPagesRepository: TopPagesRepository,
    private val configRepository: ConfigRepository,
    private val videoRepository: VideoRepository,
    private val baseSchedulers: BaseSchedulers
) : BaseViewModel() {

    companion object {
        const val SEARCH_URL = "https://www.google.com/search?q=%s"
    }

    lateinit var publishSubject: PublishSubject<String>

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

    private val _loadScript = MutableLiveData<Boolean>().apply {
        value = false
    }
    val loadScript: LiveData<Boolean> = _loadScript

    private val _listSuggestions = MutableLiveData<List<Suggestion>>().apply {
        value = listOf()
    }
    val listSuggestions: LiveData<List<Suggestion>> = _listSuggestions

    private val _progress = MutableLiveData<Int>().apply {
        value = 0
    }
    val progress: LiveData<Int> = _progress

    val changeFocusEvent = SingleLiveEvent<Boolean>()
    val showDownloadDialogEvent = SingleLiveEvent<VideoInfo>()

    @VisibleForTesting
    internal lateinit var compositeDisposable: CompositeDisposable

    override fun start() {
        compositeDisposable = CompositeDisposable()
        publishSubject = PublishSubject.create()
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
                _pageUrl.value = String.format(SEARCH_URL, input)
                _textInput.value = String.format(SEARCH_URL, input)
            }
        }
    }

    fun changeFocus(isFocus: Boolean) {
        _isFocus.value = isFocus
        changeFocusEvent.value = isFocus
    }

    fun startPage(url: String) {
        _textInput.value = url
        _isShowPage.value = true
        _isShowProgress.value = true
        _isExpandedAppbar.value = true
        verifyLinkStatus(url)
    }

    fun finishPage(url: String) {
        _textInput.value = url
        _isShowProgress.value = false
        verifyLinkStatus(url)
    }

    fun loadResource(url: String) {
        _textInput.value = url
        verifyLinkStatus(url)
        if (url.contains("facebook.com")) {
//            _pageUrl.value = ScriptUtil.FACEBOOK_SCRIPT
            _loadScript.value = true
        }
    }

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
            })
    }

    private fun getListSuggestions(): Flowable<List<Suggestion>> {
        return Flowable.combineLatest(
            publishSubject.debounce(300, TimeUnit.MILLISECONDS)
                .toFlowable(BackpressureStrategy.LATEST),
            configRepository.getSupportedPages(),
            { input, supportedPages ->
                val listSuggestions = mutableListOf<Suggestion>()
                supportedPages.filter { page -> page.name.contains(input) }.map {
                    listSuggestions.add(Suggestion(url = it.name))
                }
                listSuggestions
            }
        )
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
                    showDownloadDialogEvent.value = videoInfo
                }, { error ->
                    error.printStackTrace()
                })
        }
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
}