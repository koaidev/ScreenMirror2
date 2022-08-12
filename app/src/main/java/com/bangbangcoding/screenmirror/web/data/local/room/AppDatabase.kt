package com.bangbangcoding.screenmirror.web.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bangbangcoding.screenmirror.web.data.local.room.dao.ConfigDao
import com.bangbangcoding.screenmirror.web.data.local.room.dao.ProgressDao
import com.bangbangcoding.screenmirror.web.data.local.room.dao.VideoDao
import com.bangbangcoding.screenmirror.web.data.local.room.entity.PageInfo
import com.bangbangcoding.screenmirror.web.data.local.room.entity.ProgressInfo
import com.bangbangcoding.screenmirror.web.data.local.room.entity.SupportedPage
import com.bangbangcoding.screenmirror.web.data.local.room.entity.VideoInfo

@Database(entities = [PageInfo::class, SupportedPage::class, VideoInfo::class, ProgressInfo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun configDao(): ConfigDao

    abstract fun videoDao(): VideoDao

    abstract fun progressDao(): ProgressDao
}