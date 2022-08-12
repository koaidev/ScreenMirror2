package com.bangbangcoding.screenmirror.web.ui.progress

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class DownloadPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 1
    private val audiosFragment = AudiosFragment.newInstance()
    private val downloadedFragment = DownloadedFragment.newInstance()

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> audiosFragment
            else -> downloadedFragment
        }
    }

    fun getCurrentFragment(position: Int) : Fragment{
        return when (position) {
            1 -> audiosFragment
            else -> downloadedFragment
        }
    }
}