package com.bangbangcoding.screenmirror.web.ui.feature.mange_tab

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabsPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    val f0 = TabsFragment.newInstance(false)
    val f1 = TabsFragment.newInstance(true)

    override fun getItemCount() = 1

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> f0
            else -> f1
        }
    }
}
