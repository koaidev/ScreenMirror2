package com.bangbangcoding.screenmirror.web.ui.feature.feedback

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangbangcoding.screenmirror.BuildConfig
import com.bangbangcoding.screenmirror.web.data.remote.request.FeedbackRequest
import com.bangbangcoding.screenmirror.web.data.repository.FeedbackRepository
import com.bangbangcoding.screenmirror.web.ui.base.BaseViewModel
import com.bangbangcoding.screenmirror.web.utils.Constants
import com.bangbangcoding.screenmirror.web.utils.add
import com.bangbangcoding.screenmirror.web.utils.scheduler.BaseSchedulers
import com.bangbangcoding.screenmirror.web.utils.toSHA256String
import javax.inject.Inject

class FeedbackViewModel @Inject constructor(
    private val feedbackRepository: FeedbackRepository,
    private val baseSchedulers: BaseSchedulers
) : BaseViewModel() {

    private val _addFeedback = MutableLiveData<Boolean>()
    val addFeedback: LiveData<Boolean> = _addFeedback

    override fun start() {
//        TODO("Not yet implemented")
    }

    override fun stop() {
//        TODO("Not yet implemented")
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
                Log.e("ttt", "addFeedback: $data")
                _addFeedback.value = data.success
            }, { error ->
                Log.e("ttt", "Error: ${error.message}")
                _addFeedback.value = false
                error.printStackTrace()
            }).add(this)
    }
}
