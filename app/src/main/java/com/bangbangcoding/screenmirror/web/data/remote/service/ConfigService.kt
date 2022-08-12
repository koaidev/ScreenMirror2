package com.bangbangcoding.screenmirror.web.data.remote.service

import com.google.gson.JsonObject
import com.bangbangcoding.screenmirror.web.data.local.model.DomainAllow
import com.bangbangcoding.screenmirror.web.data.local.room.entity.PageInfo
import com.bangbangcoding.screenmirror.web.data.local.room.entity.SupportedPage
import com.bangbangcoding.screenmirror.web.data.remote.response.BaseResponse
import io.reactivex.Flowable
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ConfigService {

    @GET("supported_pages.json")
    fun getSupportedPages(): Flowable<List<SupportedPage>>

    @GET("top_pages.json")
    fun getTopPages(): Flowable<List<PageInfo>>

    @GET
    fun getInstagramData(
        @Url Value: String?,
        @Header("Cookie") cookie: String?,
        @Header("User-Agent") userAgent: String?
    ): Call<JsonObject?>?

    @POST("api/feedback")
    fun addFeedback(@Body request: RequestBody): Observable<BaseResponse>


    @GET("domain/allow-list")
    fun getDomain(): Observable<List<DomainAllow>>

    @DELETE("domain/allow-list")
    fun deleteTopPage(domainAllow: DomainAllow)

    @GET("domain/block-list")
    fun getBlockDomain(): Observable<List<DomainAllow>>
}