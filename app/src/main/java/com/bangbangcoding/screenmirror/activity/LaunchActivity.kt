package com.bangbangcoding.screenmirror.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.databinding.ActivityLaunchBinding
import com.bangbangcoding.screenmirror.utils.Common.openActivity
import com.bangbangcoding.screenmirror.utils.SharePreference

class LaunchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLaunchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        Handler(Looper.getMainLooper()).postDelayed({

            if (!SharePreference.getBooleanPref(this@LaunchActivity, SharePreference.isTutorial)) {
                val intent = Intent(this@LaunchActivity, TutorialActivity::class.java)
                intent.putExtra("fromMain", false)
                startActivity(intent)
                finish()
            } else {
                openActivity(this@LaunchActivity, MainActivity::class.java)
                finish()
            }
        }, 3000)
    }
}