package com.bangbangcoding.screenmirror.db.repo

import androidx.annotation.WorkerThread
import com.bangbangcoding.screenmirror.db.dao.HistoryDao
import com.bangbangcoding.screenmirror.db.dao.ShortcutDao
import com.bangbangcoding.screenmirror.db.model.entity.History
import com.bangbangcoding.screenmirror.db.model.entity.Shortcut
import kotlinx.coroutines.flow.Flow

class WebRepository(private val shortcutDao: ShortcutDao, private val historyDao: HistoryDao) {
    val allShortcut: Flow<List<Shortcut>> = shortcutDao.getAll()
    val allHistory: Flow<List<History>> = historyDao.all

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertShortcut(shortcut: Shortcut) {
        shortcutDao.insertShortcut(shortcut)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteShortcut(shortcut: Shortcut) {
        shortcutDao.delete(shortcut)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertAllShortcut(vararg shortcut: Shortcut) {
        shortcutDao.insertAllWebSite(*shortcut)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertHistory(history: History) {
        historyDao.insertHistory(history)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAllHistory() {
        historyDao.deleteAll()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteHistory(history: History) {
        historyDao.delete(history)
    }


}