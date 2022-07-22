package com.bangbangcoding.screenmirror.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.databinding.ActivityLaunchBinding
import com.bangbangcoding.screenmirror.utils.Common.openActivity
import com.bangbangcoding.screenmirror.utils.SharePreference

class LaunchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLaunchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        Handler(Looper.getMainLooper()).postDelayed({

            if (!SharePreference.getBooleanPref(this@LaunchActivity, SharePreference.isTutorial)) {
                openActivity(this@LaunchActivity, TutorialActivity::class.java)
                finish()
            } else {
                openActivity(this@LaunchActivity, MainActivity::class.java)
                finish()
            }
        }, 3000)
    }
}