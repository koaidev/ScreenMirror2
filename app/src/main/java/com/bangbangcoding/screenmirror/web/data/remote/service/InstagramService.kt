package com.bangbangcoding.screenmirror.web.data.remote.service

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url

interface InstagramService {

    @GET
    fun getInstagramData(
        @Url Value: String?,
        @Header("Cookie") cookie: String?,
        @Header("User-Agent") userAgent: String? = null
    ): Call<JsonObject?>?

}