package com.bangbangcoding.screenmirror.web.ui.studio

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.databinding.ActivityDownloadBinding
import com.bangbangcoding.screenmirror.web.ui.WebActivity
import com.bangbangcoding.screenmirror.web.ui.studio.multidownload.Constants

class DownloadActivity : AppCompatActivity() {
    private var mActionMode: ActionMode? = null
    private var style = 0

    private lateinit var binding: ActivityDownloadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDownloadBinding.inflate(layoutInflater)
        initViews()
        val intent = intent
        if (intent != null) {
            style = intent.getIntExtra(Constants.IS_DOWNLOAD, 0)
        }
    }

    private fun initViews() {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
//        val mAdapter = StudioAdapter(this, supportFragmentManager)
//        binding.viewPager.adapter = mAdapter
        binding.tabs.setupWithViewPager(binding.viewPager)
        binding.viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
//                if (position != 0) {
//                    AdsUtilsSwitch.getSharedInstance().showInterstitialAd {}
//                }
                if (mActionMode != null) {
                    mActionMode!!.finish()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.navigation_back) {
            exit()
        }
        return true
    }

    fun onCreateActionMode(mode: ActionMode) {
        mActionMode = mode
    }

    fun onDestroyActionMode(mode: ActionMode?) {
        mActionMode = null
    }

    private fun exit() {
        if (style != 0) {
            finish()
        } else {
            val intent = Intent(
                this@DownloadActivity,
                WebActivity::class.java
            )
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        exit()
    }
}