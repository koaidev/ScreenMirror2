package com.bangbangcoding.screenmirror.web.data.local

import com.bangbangcoding.screenmirror.web.data.local.room.dao.VideoDao
import com.bangbangcoding.screenmirror.web.data.local.room.entity.VideoInfo
import com.bangbangcoding.screenmirror.web.data.repository.VideoRepository
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoLocalDataSource @Inject constructor(
    private val videoDao: VideoDao
) : VideoRepository {

    override fun getVideoInfo(url: String): Flowable<VideoInfo> {
        return videoDao.getVideoById(url).toFlowable()
    }

    override fun saveVideoInfo(videoInfo: VideoInfo) {
        videoDao.insertVideo(videoInfo)
    }

}