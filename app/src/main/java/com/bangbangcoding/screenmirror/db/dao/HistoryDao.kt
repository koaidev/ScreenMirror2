package com.bangbangcoding.screenmirror.db.dao

import androidx.room.*
import com.bangbangcoding.screenmirror.db.model.entity.History
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT COUNT(*) FROM history")
    fun count(): Long

    @get:Query("SELECT * FROM history ORDER BY id ASC")
    val all: Flow<List<History>>

    @Query("SELECT * FROM history LIMIT ((:pageNo - 1) * :pageSize), :pageSize")
    fun getHistory(pageNo: Int, pageSize: Int): Flow<List<History>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertHistory(history: History): Long

    @Delete
    fun delete(history: History)

    @Query("DELETE FROM history")
    fun deleteAll()
}