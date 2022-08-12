package com.bangbangcoding.screenmirror.web.utils.fragment

import androidx.fragment.app.Fragment
import com.bangbangcoding.screenmirror.web.ui.home.HomeFragment
import com.bangbangcoding.screenmirror.web.ui.progress.DownloadFragment
import com.bangbangcoding.screenmirror.web.ui.video.VideosFragment
import javax.inject.Inject

interface FragmentFactory {
    fun createHomeFragment(): Fragment
    fun createProgressFragment(): Fragment
    fun createVideoFragment(): Fragment

    //    fun createSettingsFragment(): Fragment
//    fun createPasteLinkFragment(): Fragment
}

class FragmentFactoryImpl @Inject constructor() : FragmentFactory {
    override fun createHomeFragment() = HomeFragment.newInstance()

    override fun createProgressFragment() = DownloadFragment.newInstance()

    override fun createVideoFragment() = VideosFragment.newInstance()

//    override fun createSettingsFragment() = PasteLinkFragment.newInstance()

//    override fun createPasteLinkFragment() = PasteLinkFragment.newInstance()
}