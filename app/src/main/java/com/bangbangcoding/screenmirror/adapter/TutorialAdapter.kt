package com.bangbangcoding.screenmirror.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bangbangcoding.screenmirror.fragment.TutorialFragment

class TutorialAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {
    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment = TutorialFragment(position)
}