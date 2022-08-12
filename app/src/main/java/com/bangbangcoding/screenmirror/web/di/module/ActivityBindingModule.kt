package com.bangbangcoding.screenmirror.web.di.module

import com.bangbangcoding.screenmirror.web.di.ActivityScoped
import com.bangbangcoding.screenmirror.web.di.module.activity.MainModule
import com.bangbangcoding.screenmirror.web.ui.WebActivity
import com.bangbangcoding.screenmirror.web.ui.player.VideoPlayerActivity
import com.bangbangcoding.screenmirror.web.ui.widget.LightningView
import com.bangbangcoding.screenmirror.web.utils.IntentActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by ThaoBKN on 25/11/2021
 */

@Module
internal abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector
    internal abstract fun bindLightningView(): LightningView

    @ActivityScoped
    @ContributesAndroidInjector(modules = [MainModule::class])
    internal abstract fun bindMainActivity(): WebActivity

    @ActivityScoped
    @ContributesAndroidInjector
    internal abstract fun bindIntentActivity(): IntentActivity


    @ActivityScoped
    @ContributesAndroidInjector
    internal abstract fun bindVideoPlayerFragment123(): VideoPlayerActivity

//
//    @ActivityScoped
//    @ContributesAndroidInjector(modules = [VideoPlayerModule::class])
//    internal abstract fun bindVideoPlayerActivity(): VideoPlayerActivity
}