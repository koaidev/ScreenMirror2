package com.bangbangcoding.screenmirror.web.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bangbangcoding.screenmirror.web.data.local.room.entity.PageInfo
import com.bangbangcoding.screenmirror.web.data.local.room.entity.SupportedPage
import io.reactivex.Maybe

@Dao
interface ConfigDao {

    @Query("SELECT * FROM PageInfo")
    fun getAllTopPages(): Maybe<List<PageInfo>>

    @Query("SELECT * FROM SupportedPage")
    fun getSupportedPages(): Maybe<List<SupportedPage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPage(pageInfo: PageInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSupportedPage(supportedPage: SupportedPage)
}