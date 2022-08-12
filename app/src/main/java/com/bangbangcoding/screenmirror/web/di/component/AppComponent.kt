package com.bangbangcoding.screenmirror.web.di.component

import com.bangbangcoding.screenmirror.web.KunApplication
import com.bangbangcoding.screenmirror.web.di.module.ActivityBindingModule
import com.bangbangcoding.screenmirror.web.di.module.AppModule
import com.bangbangcoding.screenmirror.web.di.module.ConfigModule
import com.bangbangcoding.screenmirror.web.di.module.DatabaseModule
import com.bangbangcoding.screenmirror.web.di.module.FragmentModule
import com.bangbangcoding.screenmirror.web.di.module.NetworkModule
import com.bangbangcoding.screenmirror.web.di.module.RepositoryModule
import com.bangbangcoding.screenmirror.web.di.module.ViewModelModule
import com.bangbangcoding.screenmirror.web.ui.search.SuggestionsAdapter
import com.bangbangcoding.screenmirror.web.ui.studio.DownloadHandler
import com.bangbangcoding.screenmirror.web.ui.widget.LightningChromeClient
import com.bangbangcoding.screenmirror.web.ui.widget.LightningDownloadListener
import com.bangbangcoding.screenmirror.web.ui.widget.LightningView
import com.bangbangcoding.screenmirror.web.ui.widget.LightningWebClient
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Created by ThaoBKN on 25/11/2021
 */

@Singleton
@Component(
    modules = [AndroidSupportInjectionModule::class, AppModule::class, ActivityBindingModule::class,
        /* UtilModule::class,*/FragmentModule::class, ConfigModule::class,
        DatabaseModule::class, NetworkModule::class, RepositoryModule::class, ViewModelModule::class]
)
interface AppComponent : AndroidInjector<KunApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: KunApplication): Builder

        fun build(): AppComponent
    }

    fun inject(chromeClient: LightningChromeClient)

    fun inject(lightningWebClient: LightningWebClient)

    fun inject(suggestionsAdapter: SuggestionsAdapter)

    fun inject(lightningView: LightningView)

    //Download listener
    fun inject(lightningDownloadListener: LightningDownloadListener)

    fun inject(downloadHandler: DownloadHandler)

}
