package com.bangbangcoding.screenmirror.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bangbangcoding.screenmirror.db.dao.HistoryDao
import com.bangbangcoding.screenmirror.db.dao.ShortcutDao
import com.bangbangcoding.screenmirror.db.model.entity.History
import com.bangbangcoding.screenmirror.db.model.entity.Shortcut
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Shortcut::class, History::class], version = 1, exportSchema = false)
abstract class AppDB : RoomDatabase() {
    abstract fun shortDao(): ShortcutDao
    abstract fun historyDao(): HistoryDao

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val shortcutDao = database.shortDao()

                    // Add default shortcut.
                    val shortcutAdd = Shortcut(null, "Add", "", false)
                    val shortcutYoutube = Shortcut(null, "Youtube", "https://youtube.com", false)
                    val shortcutFacebook = Shortcut(null, "Facebook", "https://facebook.com", false)
                    val shortcutTiktok = Shortcut(null, "Tiktok", "https://tiktok.com", false)
                    shortcutDao.insertAllWebSite(
                        shortcutAdd,
                        shortcutFacebook,
                        shortcutYoutube,
                        shortcutTiktok
                    )
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDB? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDB {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDB::class.java,
                    "app_db"
                ).addCallback(DatabaseCallback(scope))
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
