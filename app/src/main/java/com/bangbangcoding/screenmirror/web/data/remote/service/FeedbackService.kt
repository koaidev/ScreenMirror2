package com.bangbangcoding.screenmirror.web.data.remote.service

import com.bangbangcoding.screenmirror.web.data.remote.request.FeedbackRequest
import com.bangbangcoding.screenmirror.web.data.remote.response.BaseResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface FeedbackService {

    @POST("feedback/")
    fun addFeedback(@Body request: FeedbackRequest): Observable<BaseResponse>
}