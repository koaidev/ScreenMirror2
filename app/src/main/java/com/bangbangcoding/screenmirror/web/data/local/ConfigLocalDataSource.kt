package com.bangbangcoding.screenmirror.web.data.local

import com.bangbangcoding.screenmirror.web.data.local.room.dao.ConfigDao
import com.bangbangcoding.screenmirror.web.data.local.room.entity.SupportedPage
import com.bangbangcoding.screenmirror.web.data.repository.ConfigRepository
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigLocalDataSource @Inject constructor(
    private val configDao: ConfigDao
) : ConfigRepository {

    override fun getSupportedPages(): Flowable<List<SupportedPage>> {
        return configDao.getSupportedPages().toFlowable()
    }

    override fun saveSupportedPages(supportedPages: List<SupportedPage>) {
        supportedPages.map { configDao.insertSupportedPage(it) }
    }
}