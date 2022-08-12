package com.bangbangcoding.screenmirror.web.data.repository

import com.google.gson.JsonObject
import com.bangbangcoding.screenmirror.web.di.qualifier.LocalData
import com.bangbangcoding.screenmirror.web.di.qualifier.RemoteData
import retrofit2.Call
import javax.inject.Inject
import javax.inject.Singleton

interface InstagramRepository {
    fun getTopPages(url : String?, cookie: String?): Call<JsonObject?>?
}

@Singleton
class InstagramRepositoryImpl @Inject constructor(
    @LocalData private val localDataSource: InstagramRepository,
    @RemoteData private val remoteDataSource: InstagramRepository
) : InstagramRepository {

    override fun getTopPages(url : String?, cookie: String?): Call<JsonObject?>? {
        return remoteDataSource.getTopPages(url, cookie)
    }
}