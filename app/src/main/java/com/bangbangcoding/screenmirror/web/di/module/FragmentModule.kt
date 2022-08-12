package com.bangbangcoding.screenmirror.web.di.module

import com.bangbangcoding.screenmirror.web.di.FragmentScoped
import com.bangbangcoding.screenmirror.web.ui.BlankFragment
import com.bangbangcoding.screenmirror.web.ui.feature.bookmarks.BookmarksFragment
import com.bangbangcoding.screenmirror.web.ui.feature.feedback.FeedbackFragment
import com.bangbangcoding.screenmirror.web.ui.feature.help.HelpFragment
import com.bangbangcoding.screenmirror.web.ui.feature.history.HistoryFragment
import com.bangbangcoding.screenmirror.web.ui.feature.mange_tab.ManagementTabFragment
import com.bangbangcoding.screenmirror.web.ui.feature.mange_tab.TabsFragment
import com.bangbangcoding.screenmirror.web.ui.feature.more_setting.MoreSettingFragment
import com.bangbangcoding.screenmirror.web.ui.feature.privacy.PrivacyFragment
import com.bangbangcoding.screenmirror.web.ui.feature.select_path.PathDownloadFragment
import com.bangbangcoding.screenmirror.web.ui.paste.fragment.BuzzPasteLinkFragment

import com.bangbangcoding.screenmirror.web.ui.progress.AudiosFragment
import com.bangbangcoding.screenmirror.web.ui.progress.DownloadFragment
import com.bangbangcoding.screenmirror.web.ui.progress.DownloadedFragment
import com.bangbangcoding.screenmirror.web.ui.progress.ProcessingFragment
import com.bangbangcoding.screenmirror.web.ui.setting.SettingFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun bindDownloadedFragment(): DownloadedFragment

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun bindProcessingFragment(): ProcessingFragment

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun bindAudiosFragment(): AudiosFragment

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun bindHistoryFragment(): HistoryFragment

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun bindSettingFragment(): SettingFragment

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun bindFeedbackFragment(): FeedbackFragment

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun bindPrivacyFragment(): PrivacyFragment

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun bindHelpFragment(): HelpFragment

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun bindMoreSettingFragment(): MoreSettingFragment

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun bindPathDownloadFragment(): PathDownloadFragment
//
//    @FragmentScoped
//    @ContributesAndroidInjector
//    abstract fun bindVideoPlayerFragment(): VideoPlayerFragment


    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun bindManagementTabFragment(): ManagementTabFragment

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun bindBuzzPasteLinkFragment(): BuzzPasteLinkFragment

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun bindTabsFragment(): TabsFragment

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun bindBlankFragment(): BlankFragment

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun bindProgressFragment(): DownloadFragment

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun bindBookmarksFragment(): BookmarksFragment
}