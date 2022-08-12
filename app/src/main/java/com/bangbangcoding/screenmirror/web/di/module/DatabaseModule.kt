package com.bangbangcoding.screenmirror.web.di.module

import androidx.room.Room
import com.bangbangcoding.screenmirror.web.KunApplication
import com.bangbangcoding.screenmirror.web.data.local.room.AppDatabase
import com.bangbangcoding.screenmirror.web.data.local.room.dao.ConfigDao
import com.bangbangcoding.screenmirror.web.data.local.room.dao.ProgressDao
import com.bangbangcoding.screenmirror.web.data.local.room.dao.VideoDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(application: KunApplication): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "dl.db").build()
    }

    @Singleton
    @Provides
    fun provideConfigDao(database: AppDatabase): ConfigDao = database.configDao()

    @Singleton
    @Provides
    fun provideCommentDao(database: AppDatabase): VideoDao = database.videoDao()

    @Singleton
    @Provides
    fun provideProgressDao(database: AppDatabase): ProgressDao = database.progressDao()
}