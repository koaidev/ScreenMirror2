package com.bangbangcoding.screenmirror.web.data.repository

import com.bangbangcoding.screenmirror.web.data.local.room.entity.ProgressInfo
import com.bangbangcoding.screenmirror.web.di.qualifier.LocalData
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton

interface ProgressRepository {

    fun getProgressInfos(): Flowable<List<ProgressInfo>>

    fun saveProgressInfo(progressInfo: ProgressInfo)

    fun deleteProgressInfo(progressInfo: ProgressInfo)
}

@Singleton
class ProgressRepositoryImpl @Inject constructor(
    @LocalData private val localDataSource: ProgressRepository
) : ProgressRepository {

    override fun getProgressInfos(): Flowable<List<ProgressInfo>> {
        return localDataSource.getProgressInfos()
    }

    override fun saveProgressInfo(progressInfo: ProgressInfo) {
        localDataSource.saveProgressInfo(progressInfo)
    }

    override fun deleteProgressInfo(progressInfo: ProgressInfo) {
        localDataSource.deleteProgressInfo(progressInfo)
    }
}