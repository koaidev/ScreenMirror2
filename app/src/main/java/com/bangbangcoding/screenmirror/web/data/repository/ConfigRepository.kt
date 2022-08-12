package com.bangbangcoding.screenmirror.web.data.repository

import androidx.annotation.VisibleForTesting
import com.bangbangcoding.screenmirror.web.data.local.room.entity.SupportedPage
import com.bangbangcoding.screenmirror.web.di.qualifier.LocalData
import com.bangbangcoding.screenmirror.web.di.qualifier.RemoteData
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton

interface ConfigRepository {

    fun getSupportedPages(): Flowable<List<SupportedPage>>

    fun saveSupportedPages(supportedPages: List<SupportedPage>)
}

@Singleton
class ConfigRepositoryImpl @Inject constructor(
    @LocalData private val localDataSource: ConfigRepository,
    @RemoteData private val remoteDataSource: ConfigRepository
) : ConfigRepository {

    @VisibleForTesting
    internal var cachedSupportedPages = listOf<SupportedPage>()

    override fun getSupportedPages(): Flowable<List<SupportedPage>> {

        return if (cachedSupportedPages.isNotEmpty()) {
            Flowable.just(cachedSupportedPages)
        } else {
            getAndCacheLocalSupportedPages()
                .flatMap { supportedPages ->
                    if (supportedPages.isEmpty()) {
                        getAndSaveRemoteSupportedPages()
                    } else {
                        Flowable.just(supportedPages)
                    }
                }
        }
    }

    override fun saveSupportedPages(supportedPages: List<SupportedPage>) {
        remoteDataSource.saveSupportedPages(supportedPages)
        localDataSource.saveSupportedPages(supportedPages)
        cachedSupportedPages = supportedPages
    }

    private fun getAndCacheLocalSupportedPages(): Flowable<List<SupportedPage>> {
        return localDataSource.getSupportedPages()
            .doOnNext { supportedPages ->
                cachedSupportedPages = supportedPages
            }
    }

    private fun getAndSaveRemoteSupportedPages(): Flowable<List<SupportedPage>> {
        return remoteDataSource.getSupportedPages()
            .doOnNext { supportedPages ->
                localDataSource.saveSupportedPages(supportedPages)
                cachedSupportedPages = supportedPages
            }
    }
}