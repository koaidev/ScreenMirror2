package com.bangbangcoding.screenmirror.web.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bangbangcoding.screenmirror.web.utils.fragment.FragmentFactory

class MainPagerAdapter constructor(
    fa: FragmentActivity,
    private val fragmentFactory: FragmentFactory
) : FragmentStateAdapter(fa) {
    //    private val f0 : Fragment = fragmentFactory.createBrowserFragment()
    private val f0: Fragment = fragmentFactory.createHomeFragment()
    private val f1: Fragment = BlankFragment.newInstance()
    private val f2: Fragment = BlankFragment.newInstance()
//    private val f1 : Fragment = fragmentFactory.createVideoFragment()
//    private val f2 : Fragment = fragmentFactory.createProgressFragment()

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> f0
            1 -> f1
            else -> f2
        }
    }

    fun getFragment(position: Int): Fragment {
        return when (position) {
            0 -> f0
            1 -> f1
            else -> f2
        }
    }
}
