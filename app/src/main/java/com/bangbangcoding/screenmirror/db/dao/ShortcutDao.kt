package com.bangbangcoding.screenmirror.db.dao

import androidx.annotation.Keep
import androidx.room.*
import com.bangbangcoding.screenmirror.db.model.entity.Shortcut
import kotlinx.coroutines.flow.Flow

@Keep
@Dao
interface ShortcutDao {
    @Query("SELECT COUNT(*) FROM shortcut")
    fun count(): Long

    @Query("SELECT * FROM shortcut ORDER BY id ASC")
    fun getAll(): Flow<List<Shortcut>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertShortcut(webSite: Shortcut)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllWebSite(vararg webSites: Shortcut)

    @Delete
    fun delete(shortcut: Shortcut)

}