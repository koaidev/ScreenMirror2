package com.bangbangcoding.screenmirror.web.di.module

import android.app.Application
import android.content.Context
import com.bangbangcoding.screenmirror.web.KunApplication
import com.bangbangcoding.screenmirror.web.di.qualifier.ApplicationContext
import com.bangbangcoding.screenmirror.web.utils.scheduler.BaseSchedulers
import com.bangbangcoding.screenmirror.web.utils.scheduler.BaseSchedulersImpl
import dagger.Binds
import dagger.Module
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Created by ThaoBKN on 25/11/2021
 */

@Module
abstract class AppModule {

    @Binds
    @ApplicationContext
    abstract fun bindApplicationContext(application: KunApplication): Context

    @Binds
    abstract fun bindApplication(application: KunApplication): Application

    @Singleton
    @Binds
    abstract fun bindBaseSchedulers(baseSchedulers: BaseSchedulersImpl): BaseSchedulers
}


@Qualifier
@Retention(AnnotationRetention.SOURCE)
annotation class MainHandler

@Qualifier
@Retention(AnnotationRetention.SOURCE)
annotation class UserPrefs

@Qualifier
@Retention(AnnotationRetention.SOURCE)
annotation class DevPrefs

@Qualifier
@Retention(AnnotationRetention.SOURCE)
annotation class MainScheduler

@Qualifier
@Retention(AnnotationRetention.SOURCE)
annotation class DiskScheduler

@Qualifier
@Retention(AnnotationRetention.SOURCE)
annotation class NetworkScheduler

@Qualifier
@Retention(AnnotationRetention.SOURCE)
annotation class DatabaseScheduler
//
//    @Provides
//    fun provideBuildInfo() = buildInfo
//
//    @Provides
//    @MainHandler
//    fun provideMainHandler() = Handler(Looper.getMainLooper())
//
//    @Provides
//    fun provideApplication(app: KunApplication): Application = browserApp
//
//    @Provides
//    fun provideContext(app: KunApplication): Context = app.applicationContext
//
//    @Provides
//    @DevPrefs
//    fun provideUserPreferences(app: KunApplication): SharedPreferences = app.getSharedPreferences("developer_settings", 0)
//
//    @Provides
//    @UserPrefs
//    fun provideDebugPreferences(app: KunApplication): SharedPreferences =
//        app.getSharedPreferences("settings", 0)
//
//
//    @Singleton
//    @Provides
//    internal fun provideSharedPreferences(app: Application): SharedPreferences {
//        return PreferenceManager.getDefaultSharedPreferences(app)
//    }
//
//    @Singleton
//    @Provides
//    internal fun provideApplicationPreferences(sharedPreferences: SharedPreferences): ApplicationPreferences {
//        return ApplicationPreferences(sharedPreferences)
//    }
//
//
//    @Singleton
//    @Provides
//    fun bindBaseSchedulers(baseSchedulers: BaseSchedulersImpl): BaseSchedulers {
//        return baseSchedulers
//    }
//
//
//    @Provides
//    fun providesClipboardManager() = browserApp.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//
//    @Provides
//    fun providesInputMethodManager() = browserApp.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//
//    @Provides
//    fun providesDownloadManager() = browserApp.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//
//    @Provides
//    fun providesConnectivityManager() = browserApp.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//
//    @Provides
//    fun providesNotificationManager() = browserApp.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//    @Provides
//    fun providesWindowManager() = browserApp.getSystemService(Context.WINDOW_SERVICE) as WindowManager
//
//    @RequiresApi(Build.VERSION_CODES.N_MR1)
//    @Provides
//    fun providesShortcutManager() = browserApp.getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager
//
//    @Provides
//    @DatabaseScheduler
//    @Singleton
//    fun providesIoThread(): Scheduler = Schedulers.from(Executors.newSingleThreadExecutor())
//
//    @Provides
//    @DiskScheduler
//    @Singleton
//    fun providesDiskThread(): Scheduler = Schedulers.from(Executors.newSingleThreadExecutor())
//
//    @Provides
//    @NetworkScheduler
//    @Singleton
//    fun providesNetworkThread(): Scheduler = Schedulers.from(ThreadPoolExecutor(0, 4, 60, TimeUnit.SECONDS, LinkedBlockingDeque()))
//
//    @Provides
//    @MainScheduler
//    @Singleton
//    fun providesMainThread(): Scheduler = AndroidSchedulers.mainThread()
//
//    @Singleton
//    @Provides
//    fun providesSuggestionsCacheControl(): CacheControl = CacheControl.Builder().maxStale(1, TimeUnit.DAYS).build()
//
//    @Provides
//    @Singleton
//    fun provideLogger(): Logger = if (BuildConfig.DEBUG) {
//        AndroidLogger()
//    } else {
//        NoOpLogger()
//    }
//
//    @Provides
//    @Singleton
//    fun provideI2PAndroidHelper(): I2PAndroidHelper = I2PAndroidHelper(browserApp)
//
//    @Provides
//    fun providesListPageReader(): ListPageReader = MezzanineGenerator.ListPageReader()
//
//    @Provides
//    fun providesHomePageReader(): HomePageReader = MezzanineGenerator.HomePageReader()
//
//    @Provides
//    fun providesBookmarkPageReader(): BookmarkPageReader = MezzanineGenerator.BookmarkPageReader()
//}
//
//
//@Qualifier
//@Retention(AnnotationRetention.SOURCE)
//annotation class MainHandler
//
//@Qualifier
//@Retention(AnnotationRetention.SOURCE)
//annotation class UserPrefs
//
//@Qualifier
//@Retention(AnnotationRetention.SOURCE)
//annotation class DevPrefs
//
//@Qualifier
//@Retention(AnnotationRetention.SOURCE)
//annotation class MainScheduler
//
//@Qualifier
//@Retention(AnnotationRetention.SOURCE)
//annotation class DiskScheduler
//
//@Qualifier
//@Retention(AnnotationRetention.SOURCE)
//annotation class NetworkScheduler
//
//@Qualifier
//@Retention(AnnotationRetention.SOURCE)
//annotation class DatabaseScheduler