package com.bangbangcoding.screenmirror.web.data.remote

import com.bangbangcoding.screenmirror.web.data.local.room.entity.SupportedPage
import com.bangbangcoding.screenmirror.web.data.remote.service.ConfigService
import com.bangbangcoding.screenmirror.web.data.repository.ConfigRepository
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigRemoteDataSource @Inject constructor(
    private val configService: ConfigService
) : ConfigRepository {

    override fun getSupportedPages(): Flowable<List<SupportedPage>> {
        return configService.getSupportedPages()
    }

    override fun saveSupportedPages(supportedPages: List<SupportedPage>) {
    }
}