package com.bangbangcoding.screenmirror.web

import android.content.Context
import androidx.multidex.MultiDex
import com.bangbangcoding.screenmirror.db.AppDB
import com.bangbangcoding.screenmirror.db.repo.WebRepository
import com.bangbangcoding.screenmirror.web.di.component.AppComponent
import com.bangbangcoding.screenmirror.web.di.component.DaggerAppComponent
import com.bangbangcoding.screenmirror.web.utils.Constants
import com.facebook.stetho.Stetho
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/**
 * Created by ThaoBKN on 25/11/2021
 */


open class KunApplication : DaggerApplication() {

    companion object {
        @JvmStatic
        lateinit var appComponent: AppComponent

        @JvmField
        var mInstance: KunApplication? = null

        @JvmStatic
        fun getAppInstance(): KunApplication {
            return mInstance as KunApplication
        }
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this

        // Initialize Stetho
        Stetho.initializeWithDefaults(this)

        MobileAds.initialize(this) {

        }

//        val configuration = RequestConfiguration.Builder().setTestDeviceIds(listOf("FB733A29AFF94D9AA8DC2CBCBC9641E7")).build()
        val configuration = RequestConfiguration.Builder().build()
        MobileAds.setRequestConfiguration(configuration)
        Constants.setConstants()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)

        appComponent = DaggerAppComponent.builder().application(this).build()
    }

    public override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
        appComponent

    private val applicationScope = CoroutineScope(SupervisorJob())
    private val database by lazy { AppDB.getDatabase(this, applicationScope) }
    val repository by lazy { WebRepository(database.shortDao(), database.historyDao()) }
}