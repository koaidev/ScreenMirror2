package com.bangbangcoding.screenmirror.web.data.remote

import com.google.gson.JsonObject
import com.bangbangcoding.screenmirror.web.data.remote.service.InstagramService
import com.bangbangcoding.screenmirror.web.data.repository.InstagramRepository
import retrofit2.Call
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InstagramRemoteDataSource @Inject constructor(
    private val instagramService: InstagramService
) : InstagramRepository {

    override fun getTopPages(url : String?, cookie: String?): Call<JsonObject?>? {
        return instagramService.getInstagramData(url, cookie)
    }
}