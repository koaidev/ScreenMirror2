package com.bangbangcoding.screenmirror.web.ui.progress

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.databinding.FragmentDownloadBinding
import com.bangbangcoding.screenmirror.web.ui.base.BaseFragment
import com.bangbangcoding.screenmirror.web.ui.feature.feedback.FeedbackFragment
import com.bangbangcoding.screenmirror.web.ui.setting.SettingFragment
import com.bangbangcoding.screenmirror.web.utils.IntentActivity
import javax.inject.Inject

class DownloadFragment : BaseFragment() {

    companion object {
        fun newInstance() = DownloadFragment()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: DownloadViewModel

    private var _binding: FragmentDownloadBinding? = null
    private val binding get() = _binding!!

    private lateinit var downloadPagerAdapter: DownloadPagerAdapter
    private var firstMenu: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this, viewModelFactory)[DownloadViewModel::class.java]

        _binding = FragmentDownloadBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun setupUI() {
        super.setupUI()
        binding.viewPager.isUserInputEnabled = false
        downloadPagerAdapter = DownloadPagerAdapter(this)
        binding.viewPager.adapter = downloadPagerAdapter
        binding.viewPager.registerOnPageChangeCallback(onPageChangeListener)

        setToolbar1()
        setToolbar2()

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            val tabName = when (position) {
                0 -> {
                    "VIDEOS"
                }
                else -> {
                    "AUDIOS"
                }
            }

            tab.text = tabName
        }.attach()

//        binding.navView.setOnItemSelectedListener(itemSelectedListener)
    }

    private fun setToolbar1() {
        binding.toolbar.setTitle(R.string.txt_download)
        binding.toolbar.inflateMenu(R.menu.menu_download)
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_tool_bar)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.toolbar.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
        }
        binding.toolbar.menu.findItem(R.id.navigation_delete).isVisible =
            Build.VERSION.SDK_INT != Build.VERSION_CODES.Q
    }

    private fun setToolbar2() {
        binding.toolbar1.setTitle(R.string.txt_download)
        binding.toolbar1.inflateMenu(R.menu.menu_download_2)
        binding.toolbar1.setNavigationIcon(R.drawable.ic_arrow_back_tool_bar)
        binding.toolbar1.setNavigationOnClickListener {
            val fragment = downloadPagerAdapter.getCurrentFragment(binding.viewPager.currentItem)
            if (fragment is DownloadedFragment) {
                fragment.showCheckbox(false)
                fragment.checkShow(false)
            }
            binding.toolbar.visibility = View.VISIBLE
            binding.toolbar1.visibility = View.GONE
        }
        binding.toolbar1.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
        }
    }

    private val onPageChangeListener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(p0: Int) {
            //no-op
        }

        override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            //no-op
        }

        override fun onPageSelected(postion: Int) {
            binding.viewPager.currentItem = postion
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_feedback -> {
                startActivity(
                    IntentActivity.getIntent(requireContext(), FeedbackFragment::class.java)
                )
            }
            R.id.navigation_setting -> {
                startActivity(
                    IntentActivity.getIntent(requireContext(), SettingFragment::class.java)
                )
            }
            R.id.navigation_delete -> {
                firstMenu = false
                binding.toolbar.visibility = View.GONE
                binding.toolbar1.visibility = View.VISIBLE

                val fragment =
                    downloadPagerAdapter.getCurrentFragment(binding.viewPager.currentItem)
                if (fragment is DownloadedFragment) {
                    fragment.showCheckbox(true)
                }
            }
            R.id.navigation_select_all -> {
                val fragment =
                    downloadPagerAdapter.getCurrentFragment(binding.viewPager.currentItem)
                if (fragment is DownloadedFragment) {
                    fragment.checkShow(!fragment.getCheck())
                }
            }
            R.id.navigation_delete_all -> {
                val fragment =
                    downloadPagerAdapter.getCurrentFragment(binding.viewPager.currentItem)
                if (fragment is DownloadedFragment) {
                    if (!fragment.getCheck()) {
                        fragment.showCheckbox(false)
                        fragment.checkShow(false)
                        showToolbarActionWithFile()
                    } else {
                        fragment.deleteMultipleVideo {
                            fragment.showCheckbox(false)
                            fragment.checkShow(false)
                            showToolbarActionWithFile()
                        }
                    }
                }
            }
        }

        return true
    }

    fun showToolbarActionWithFile() {
        binding.toolbar.visibility = View.VISIBLE
        binding.toolbar1.visibility = View.GONE
    }
}