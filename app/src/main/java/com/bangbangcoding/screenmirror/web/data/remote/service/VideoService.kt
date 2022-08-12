package com.bangbangcoding.screenmirror.web.data.remote.service

import com.bangbangcoding.screenmirror.web.data.local.model.VideoInfoWrapper
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

interface VideoService {

    @GET("info")
    fun getVideoInfo(@Query("url") url: String): Flowable<VideoInfoWrapper>
}