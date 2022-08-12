package com.bangbangcoding.screenmirror.web.data.local

import com.bangbangcoding.screenmirror.web.data.local.room.dao.ProgressDao
import com.bangbangcoding.screenmirror.web.data.local.room.entity.ProgressInfo
import com.bangbangcoding.screenmirror.web.data.repository.ProgressRepository
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgressLocalDataSource @Inject constructor(
    private val progressDao: ProgressDao
) : ProgressRepository {

    override fun getProgressInfos(): Flowable<List<ProgressInfo>> {
        return progressDao.getProgressInfos()
    }

    override fun saveProgressInfo(progressInfo: ProgressInfo) {
        progressDao.insertProgressInfo(progressInfo)
    }

    override fun deleteProgressInfo(progressInfo: ProgressInfo) {
        progressDao.deleteProgressInfo(progressInfo)
    }
}