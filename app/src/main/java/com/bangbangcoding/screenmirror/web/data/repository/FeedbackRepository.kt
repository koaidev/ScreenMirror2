package com.bangbangcoding.screenmirror.web.data.repository

import com.bangbangcoding.screenmirror.web.data.remote.request.FeedbackRequest
import com.bangbangcoding.screenmirror.web.data.remote.response.BaseResponse
import com.bangbangcoding.screenmirror.web.data.remote.service.FeedbackService
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FeedbackRepository @Inject constructor(
    private val feedbackService: FeedbackService
) {

    fun addFeedback(request: FeedbackRequest) : Observable<BaseResponse> {
        return feedbackService.addFeedback(request)
    }

}