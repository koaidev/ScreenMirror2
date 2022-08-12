package com.bangbangcoding.screenmirror.web.di.module

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ShortcutManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import com.anthonycr.mezzanine.MezzanineGenerator
import com.bangbangcoding.screenmirror.BuildConfig
import com.bangbangcoding.screenmirror.web.KunApplication
import com.bangbangcoding.screenmirror.web.html.ListPageReader
import com.bangbangcoding.screenmirror.web.html.bookmark.BookmarkPageReader
import com.bangbangcoding.screenmirror.web.html.homepage.HomePageReader
import com.bangbangcoding.screenmirror.web.logger.AndroidLogger
import com.bangbangcoding.screenmirror.web.logger.Logger
import com.bangbangcoding.screenmirror.web.logger.NoOpLogger
import com.bangbangcoding.screenmirror.web.utils.ApplicationPreferences
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.CacheControl
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class ConfigModule {

    @Provides
    @MainHandler
    fun provideMainHandler() = Handler(Looper.getMainLooper())

//    @Singleton
//    @Provides
//    fun provideProxyUtils() = ProxyUtils()


    @Provides
    @DevPrefs
    fun provideUserPreferences(app: KunApplication): SharedPreferences =
        app.getSharedPreferences("developer_settings", 0)

    @Provides
    @Singleton
    @UserPrefs
    fun provideDebugPreferences(app: KunApplication): SharedPreferences =
        app.getSharedPreferences("settings", 0)


    @Singleton
    @Provides
    internal fun provideSharedPreferences(app: KunApplication): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(app)
    }

    @Singleton
    @Provides
    internal fun provideApplicationPreferences(sharedPreferences: SharedPreferences): ApplicationPreferences {
        return ApplicationPreferences(sharedPreferences)
    }

    @Provides
    fun providesClipboardManager(app: KunApplication) =
        app.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    @Provides
    fun providesInputMethodManager(app: KunApplication) =
        app.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    @Provides
    fun providesDownloadManager(app: KunApplication) =
        app.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    @Provides
    fun providesConnectivityManager(app: KunApplication) =
        app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Provides
    fun providesNotificationManager(app: KunApplication) =
        app.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Provides
    fun providesWindowManager(app: KunApplication) =
        app.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    @Provides
    fun providesShortcutManager(app: KunApplication) =
        app.getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager

    @Provides
    @DatabaseScheduler
    @Singleton
    fun providesIoThread(): Scheduler = Schedulers.from(Executors.newSingleThreadExecutor())

    @Provides
    @DiskScheduler
    @Singleton
    fun providesDiskThread(): Scheduler = Schedulers.from(Executors.newSingleThreadExecutor())

    @Provides
    @NetworkScheduler
    @Singleton
    fun providesNetworkThread(): io.reactivex.Scheduler =
        Schedulers.from(ThreadPoolExecutor(0, 4, 60, TimeUnit.SECONDS, LinkedBlockingDeque()))

    @Provides
    @MainScheduler
    @Singleton
    fun providesMainThread(): Scheduler = AndroidSchedulers.mainThread()

    @Singleton
    @Provides
    fun providesSuggestionsCacheControl(): CacheControl =
        CacheControl.Builder().maxStale(1, TimeUnit.DAYS).build()

    @Provides
    @Singleton
    fun provideLogger(): Logger = if (BuildConfig.DEBUG) {
        AndroidLogger()
    } else {
        NoOpLogger()
    }

//    @Provides
//    @Singleton
//    fun provideI2PAndroidHelper(app: KunApplication): I2PAndroidHelper = I2PAndroidHelper(app)

    @Provides
    fun providesListPageReader(): ListPageReader = MezzanineGenerator.ListPageReader()

    @Provides
    fun providesHomePageReader(): HomePageReader = MezzanineGenerator.HomePageReader()

    @Provides
    fun providesBookmarkPageReader(): BookmarkPageReader = MezzanineGenerator.BookmarkPageReader()
//
//    @Provides
//    fun providesDownloadHandler() = DownloadHandler()
}
