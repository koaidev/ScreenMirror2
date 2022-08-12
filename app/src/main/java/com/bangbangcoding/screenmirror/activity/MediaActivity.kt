package com.bangbangcoding.screenmirror.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.adapter.MediaPagerAdapter
import com.bangbangcoding.screenmirror.databinding.ActivityMediaBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

private val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2,
    R.string.tab_text_3
)

class MediaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)

        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init tab_layout
        val mediaPagerAdapter = MediaPagerAdapter(this)
        binding.viewPager.adapter = mediaPagerAdapter


        //init tool bar
        val toolBar = binding.toolBar
        setSupportActionBar(toolBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = resources.getString(R.string.media)

        //init tab layout
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])

        }.attach()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}