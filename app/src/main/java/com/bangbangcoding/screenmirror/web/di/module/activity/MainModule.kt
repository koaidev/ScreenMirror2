package com.bangbangcoding.screenmirror.web.di.module.activity

import com.bangbangcoding.screenmirror.web.di.ActivityScoped
import com.bangbangcoding.screenmirror.web.di.FragmentScoped
import com.bangbangcoding.screenmirror.web.ui.home.HomeFragment
import com.bangbangcoding.screenmirror.web.ui.video.VideosFragment
import com.bangbangcoding.screenmirror.web.utils.fragment.FragmentFactory
import com.bangbangcoding.screenmirror.web.utils.fragment.FragmentFactoryImpl
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainModule {

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun bindHomeFragment(): HomeFragment

//    @FragmentScoped
//    @ContributesAndroidInjector
//    abstract fun bindProgressFragment(): DownloadFragment

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun bindVideoFragment(): VideosFragment

//    @FragmentScoped
//    @ContributesAndroidInjector
//    abstract fun bindPasteLinkFragment(): PasteLinkFragment

    @ActivityScoped
    @Binds
    abstract fun bindFragmentFactory(fragmentFactory: FragmentFactoryImpl): FragmentFactory
}