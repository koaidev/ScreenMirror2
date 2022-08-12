package com.bangbangcoding.screenmirror.web.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangbangcoding.screenmirror.web.di.ViewModelKey
import com.bangbangcoding.screenmirror.web.ui.MainViewModel
import com.bangbangcoding.screenmirror.web.ui.SplashViewModel
import com.bangbangcoding.screenmirror.web.ui.feature.bookmarks.BookmarksViewModel
import com.bangbangcoding.screenmirror.web.ui.feature.feedback.FeedbackViewModel
import com.bangbangcoding.screenmirror.web.ui.feature.help.HelpViewModel
import com.bangbangcoding.screenmirror.web.ui.feature.history.HistoryViewModel
import com.bangbangcoding.screenmirror.web.ui.feature.mange_tab.ManagementTabViewModel
import com.bangbangcoding.screenmirror.web.ui.feature.mange_tab.TabsViewModel
import com.bangbangcoding.screenmirror.web.ui.feature.more_setting.MoreSettingViewModel
import com.bangbangcoding.screenmirror.web.ui.feature.select_path.PathDownloadViewModel
import com.bangbangcoding.screenmirror.web.ui.home.BrowserViewModel
import com.bangbangcoding.screenmirror.web.ui.home.HomeViewModel
import com.bangbangcoding.screenmirror.web.ui.paste.PasteLinkViewModel
import com.bangbangcoding.screenmirror.web.ui.player.VideoPlayerViewModel
import com.bangbangcoding.screenmirror.web.ui.progress.AudiosViewModel
import com.bangbangcoding.screenmirror.web.ui.progress.DashboardViewModel
import com.bangbangcoding.screenmirror.web.ui.progress.DownloadViewModel
import com.bangbangcoding.screenmirror.web.ui.progress.DownloadedViewModel
import com.bangbangcoding.screenmirror.web.ui.progress.ProcessingViewModel
import com.bangbangcoding.screenmirror.web.ui.setting.SettingViewModel
import com.bangbangcoding.screenmirror.web.ui.video.VideosViewModel
import com.bangbangcoding.screenmirror.web.utils.fragment.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module(includes = [AppModule::class])
abstract class ViewModelModule {

    @Singleton
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun bindSplashViewModel(viewModel: SplashViewModel): ViewModel

//    @Binds
//    @IntoMap
//    @ViewModelKey(MainViewModel::class)
//    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BrowserViewModel::class)
    abstract fun bindBrowserViewModel(viewModel: BrowserViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DashboardViewModel::class)
    abstract fun bindProgressViewModel(viewModel: DashboardViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VideosViewModel::class)
    abstract fun bindVideoViewModel(viewModel: VideosViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ManagementTabViewModel::class)
    abstract fun bindManagementTabViewModel(viewModel: ManagementTabViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PasteLinkViewModel::class)
    abstract fun bindPasteLinkViewModel(viewModel: PasteLinkViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingViewModel::class)
    abstract fun bindSettingsViewModel(viewModel: SettingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DownloadViewModel::class)
    abstract fun bindDownloadViewModel(viewModel: DownloadViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DownloadedViewModel::class)
    abstract fun bindDownloadedViewModel(viewModel: DownloadedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProcessingViewModel::class)
    abstract fun bindProcessingViewModel(viewModel: ProcessingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HistoryViewModel::class)
    abstract fun bindHistoryViewModel(viewModel: HistoryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FeedbackViewModel::class)
    abstract fun bindFeedbackViewModel(viewModel: FeedbackViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HelpViewModel::class)
    abstract fun bindHelpViewModel(viewModel: HelpViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MoreSettingViewModel::class)
    abstract fun bindMoreSettingViewModel(viewModel: MoreSettingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PathDownloadViewModel::class)
    abstract fun bindPathDownloadViewModel(viewModel: PathDownloadViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VideoPlayerViewModel::class)
    abstract fun bindVideoPlayerViewModel(viewModel: VideoPlayerViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AudiosViewModel::class)
    abstract fun bindAudiosViewModel(viewModel: AudiosViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TabsViewModel::class)
    abstract fun bindTabsViewModel(viewModel: TabsViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(BookmarksViewModel::class)
    abstract fun bindBookmarksViewModel(viewModel: BookmarksViewModel): ViewModel
}
