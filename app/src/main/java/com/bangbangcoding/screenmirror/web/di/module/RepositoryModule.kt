package com.bangbangcoding.screenmirror.web.di.module

import com.bangbangcoding.screenmirror.web.allowlist.AllowListModel
import com.bangbangcoding.screenmirror.web.allowlist.SessionAllowListModel
import com.bangbangcoding.screenmirror.web.data.allowlist.AdBlockAllowListDatabase
import com.bangbangcoding.screenmirror.web.data.allowlist.AdBlockAllowListRepository
import com.bangbangcoding.screenmirror.web.data.bookmark.BookmarkDatabase
import com.bangbangcoding.screenmirror.web.data.bookmark.BookmarkRepository
import com.bangbangcoding.screenmirror.web.data.downloads.DownloadsDatabase
import com.bangbangcoding.screenmirror.web.data.downloads.DownloadsRepository
import com.bangbangcoding.screenmirror.web.data.history.HistoryDatabase
import com.bangbangcoding.screenmirror.web.data.history.HistoryRepository
import com.bangbangcoding.screenmirror.web.data.local.ConfigLocalDataSource
import com.bangbangcoding.screenmirror.web.data.local.ProgressLocalDataSource
import com.bangbangcoding.screenmirror.web.data.local.TopPagesLocalDataSource
import com.bangbangcoding.screenmirror.web.data.local.VideoLocalDataSource
import com.bangbangcoding.screenmirror.web.data.remote.ConfigRemoteDataSource
import com.bangbangcoding.screenmirror.web.data.remote.InstagramRemoteDataSource
import com.bangbangcoding.screenmirror.web.data.remote.TopPagesRemoteDataSource
import com.bangbangcoding.screenmirror.web.data.remote.VideoRemoteDataSource
import com.bangbangcoding.screenmirror.web.data.repository.*
import com.bangbangcoding.screenmirror.web.di.qualifier.LocalData
import com.bangbangcoding.screenmirror.web.di.qualifier.RemoteData
import com.bangbangcoding.screenmirror.web.ssl.SessionSslWarningPreferences
import com.bangbangcoding.screenmirror.web.ssl.SslWarningPreferences
import dagger.Binds
import dagger.Module
import javax.inject.Singleton
@Module
abstract class RepositoryModule {

    @Binds
    abstract fun provideBookmarkModel(bookmarkDatabase: BookmarkDatabase): BookmarkRepository

    @Binds
    abstract fun provideDownloadsModel(downloadsDatabase: DownloadsDatabase): DownloadsRepository

    @Binds
    abstract fun providesHistoryModel(historyDatabase: HistoryDatabase): HistoryRepository

    @Binds
    abstract fun providesAdBlockAllowListModel(adBlockAllowListDatabase: AdBlockAllowListDatabase): AdBlockAllowListRepository

    @Binds
    abstract fun providesAllowListModel(sessionAllowListModel: SessionAllowListModel): AllowListModel

    @Binds
    abstract fun providesSslWarningPreferences(sessionSslWarningPreferences: SessionSslWarningPreferences): SslWarningPreferences

    @Singleton
    @Binds
    @LocalData
    abstract fun bindConfigLocalDataSource(localDataSource: ConfigLocalDataSource): ConfigRepository

    @Singleton
    @Binds
    @RemoteData
    abstract fun bindConfigRemoteDataSource(remoteDataSource: ConfigRemoteDataSource): ConfigRepository

    @Singleton
    @Binds
    abstract fun bindConfigRepositoryImpl(configRepository: ConfigRepositoryImpl): ConfigRepository

    @Singleton
    @Binds
    @LocalData
    abstract fun bindTopPagesLocalDataSource(localDataSource: TopPagesLocalDataSource): TopPagesRepository

    @Singleton
    @Binds
    @RemoteData
    abstract fun bindTopPagesRemoteDataSource(remoteDataSource: TopPagesRemoteDataSource): TopPagesRepository

    @Singleton
    @Binds
    abstract fun bindTopPagesRepositoryImpl(topPagesRepository: TopPagesRepositoryImpl): TopPagesRepository

    @Singleton
    @Binds
    @LocalData
    abstract fun bindVideoLocalDataSource(localDataSource: VideoLocalDataSource): VideoRepository

    @Singleton
    @Binds
    @RemoteData
    abstract fun bindVideoRemoteDataSource(remoteDataSource: VideoRemoteDataSource): VideoRepository

    @Singleton
    @Binds
    abstract fun bindVideoRepositoryImpl(videoRepository: VideoRepositoryImpl): VideoRepository

    @Singleton
    @Binds
    @LocalData
    abstract fun bindProgressLocalDataSource(localDataSource: ProgressLocalDataSource): ProgressRepository

    @Singleton
    @Binds
    abstract fun bindProgressRepositoryImpl(progressRepository: ProgressRepositoryImpl): ProgressRepository


    @Singleton
    @Binds
    @RemoteData
    abstract fun bindInstagramRemoteDataSource(remoteDataSource: InstagramRemoteDataSource): InstagramRepository
}