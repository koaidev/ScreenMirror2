package com.bangbangcoding.screenmirror.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.adapter.TutorialAdapter
import com.bangbangcoding.screenmirror.databinding.ActivityTutorialBinding
import com.bangbangcoding.screenmirror.utils.Common
import com.bangbangcoding.screenmirror.utils.SharePreference
import com.bangbangcoding.screenmirror.viewmodel.TutorialViewModel
import com.google.android.material.tabs.TabLayoutMediator

class TutorialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTutorialBinding
    private lateinit var viewModel: TutorialViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[TutorialViewModel::class.java]

        binding.viewPager.adapter = TutorialAdapter(this)
        //init tab layout
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->

        }.attach()

        viewModel.currentPage.observe(this) {
            if (it == 2) {
                binding.imgContinue.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_done,
                        resources.newTheme()
                    )
                )
                binding.imgContinue.setOnClickListener {
                    SharePreference.setBooleanPref(
                        this@TutorialActivity,
                        SharePreference.isTutorial,
                        true
                    )
                    Common.openActivity(this@TutorialActivity, MainActivity::class.java)
                    finish()
                }
            } else {
                binding.imgContinue.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_continue,
                        resources.newTheme()
                    )
                )
                binding.imgContinue.setOnClickListener {
                    binding.viewPager.currentItem = viewModel.currentPage.value!! + 1
                }
            }
        }
        binding.viewPager.setPageTransformer(DepthPageTransformer())

    }
}

private const val MIN_SCALE = 0.75f


class DepthPageTransformer : ViewPager2.PageTransformer {

    override fun transformPage(view: View, position: Float) {
        view.apply {
            val pageWidth = width
            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    alpha = 0f
                }
                position <= 0 -> { // [-1,0]
                    // Use the default slide transition when moving to the left page
                    alpha = 1f
                    translationX = 0f
                    translationZ = 0f
                    scaleX = 1f
                    scaleY = 1f
                }
                position <= 1 -> { // (0,1]
                    // Fade the page out.
                    alpha = 1 - position

                    // Counteract the default slide transition
                    translationX = pageWidth * -position
                    // Move it behind the left page
                    translationZ = -1f

                    // Scale the page down (between MIN_SCALE and 1)
                    val scaleFactor = (MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position)))
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                }
                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    alpha = 0f
                }
            }
        }
    }
}