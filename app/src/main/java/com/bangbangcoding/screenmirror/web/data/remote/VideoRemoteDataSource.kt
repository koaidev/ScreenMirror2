package com.bangbangcoding.screenmirror.web.data.remote

import com.bangbangcoding.screenmirror.web.data.local.room.entity.VideoInfo
import com.bangbangcoding.screenmirror.web.data.remote.service.VideoService
import com.bangbangcoding.screenmirror.web.data.repository.VideoRepository
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRemoteDataSource @Inject constructor(
    private val videoService: VideoService
) : VideoRepository {

    override fun getVideoInfo(url: String): Flowable<VideoInfo> {
        return videoService.getVideoInfo(url)
            .flatMap { videoInfoWrapper -> Flowable.just(videoInfoWrapper.videoInfo) }
    }

    override fun saveVideoInfo(videoInfo: VideoInfo) {
    }
}