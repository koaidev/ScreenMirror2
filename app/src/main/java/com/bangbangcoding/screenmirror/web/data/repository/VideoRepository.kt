package com.bangbangcoding.screenmirror.web.data.repository

import androidx.annotation.VisibleForTesting
import com.bangbangcoding.screenmirror.web.data.local.room.entity.VideoInfo
import com.bangbangcoding.screenmirror.web.di.qualifier.LocalData
import com.bangbangcoding.screenmirror.web.di.qualifier.RemoteData
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton

interface VideoRepository {

    fun getVideoInfo(url: String): Flowable<VideoInfo>

    fun saveVideoInfo(videoInfo: VideoInfo)
}

@Singleton
class VideoRepositoryImpl @Inject constructor(
    @LocalData private val localDataSource: VideoRepository,
    @RemoteData private val remoteDataSource: VideoRepository
) : VideoRepository {

    @VisibleForTesting
    internal var cachedVideos: MutableMap<String, VideoInfo> = mutableMapOf()

    override fun getVideoInfo(url: String): Flowable<VideoInfo> {
        cachedVideos[url]?.let { return Flowable.just(it) }

        val localVideo = getAndCacheLocalVideo(url)
        val remoteVideo = getAndSaveRemoteVideo(url)
        return Flowable.concat(localVideo, remoteVideo).take(1)
    }

    override fun saveVideoInfo(videoInfo: VideoInfo) {
        remoteDataSource.saveVideoInfo(videoInfo)
        localDataSource.saveVideoInfo(videoInfo)
        cachedVideos[videoInfo.originalUrl] = videoInfo
    }

    private fun getAndCacheLocalVideo(url: String): Flowable<VideoInfo> {
        return localDataSource.getVideoInfo(url)
            .doOnNext { videoInfo ->
                cachedVideos[url] = videoInfo
            }
    }

    private fun getAndSaveRemoteVideo(url: String): Flowable<VideoInfo> {
        return remoteDataSource.getVideoInfo(url)
            .doOnNext { videoInfo ->
                videoInfo.originalUrl = url
                localDataSource.saveVideoInfo(videoInfo)
                cachedVideos[url] = videoInfo
            }
    }
}