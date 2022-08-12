package com.bangbangcoding.screenmirror.web.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.databinding.FragmentSettingBinding
import com.bangbangcoding.screenmirror.web.ui.base.BaseFragment
import com.bangbangcoding.screenmirror.web.ui.feature.help.HelpFragment
import com.bangbangcoding.screenmirror.web.ui.feature.more_setting.MoreSettingFragment
import com.bangbangcoding.screenmirror.web.ui.preference.UserPreferences
import com.bangbangcoding.screenmirror.web.utils.Constants
import com.bangbangcoding.screenmirror.web.utils.IntentActivity
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import javax.inject.Inject

class SettingFragment : BaseFragment() {


    companion object {
        fun newInstance() = SettingFragment()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var userPreferences: UserPreferences

    private var _binding: FragmentSettingBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: SettingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel =
            ViewModelProvider(this, viewModelFactory)[SettingViewModel::class.java]

        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun setupUI() {
        binding.toolbarLayout.tvTitle.text = getString(R.string.txt_menu_setting)
        binding.tvPath.text = Constants.getExternalFile().absolutePath
        binding.switchSync.isChecked = userPreferences.downloadOnlyWifi

        binding.switchSync.setOnCheckedChangeListener { _, isChecked ->
            userPreferences.downloadOnlyWifi = isChecked
        }

        binding.tvDownloadWifi.setOnClickListener {
            binding.switchSync.isChecked = !binding.switchSync.isChecked
        }

        binding.toolbarLayout.ivBackToolbar.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.tvRemoveADS.setOnClickListener {
            removeADS()
        }

        binding.tvHelp.setOnClickListener {
            startActivity(
                IntentActivity.getIntent(requireContext(), HelpFragment::class.java)
            )
        }

        binding.tvMoreSetting.setOnClickListener {
            startActivity(
                IntentActivity.getIntent(requireContext(), MoreSettingFragment::class.java)
            )
        }

        binding.tvDownloadLocation.setOnClickListener {
//            startActivity(
//                IntentActivity.getIntent(requireContext(), PathDownloadFragment::class.java)
//            )
        }

//        val adRequest1: AdRequest = AdRequest.Builder();
        // Step 1 - Create an AdView and set the ad unit ID on it.
//        AdsUtils.loadBanner(requireActivity(), binding.adContainerView)
        loadNativeAds()
    }

//    private fun loadAds() {
//        val adRequest1: AdRequest = AdRequest.Builder().build()
//        binding.adView.loadAd(adRequest1)
//    }

    private fun removeADS() {
        Toast.makeText(requireContext(), "Remove ADS", Toast.LENGTH_SHORT).show()
    }

    private fun loadNativeAds() {
        val adLoader = AdLoader.Builder(requireContext(), Constants.ADS_NATIVE_ID)
            .forNativeAd { ad: NativeAd ->
                // Show the ad.
                val styles = NativeTemplateStyle.Builder().build()
                binding.myTemplate.setStyles(styles)
                binding.myTemplate.setNativeAd(ad)
                binding.myTemplate.visibility = View.VISIBLE
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    if (context != null) {
                        binding.myTemplate.visibility = View.GONE
                    }
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .build()
            )
            .build()

        adLoader.loadAds(AdRequest.Builder().build(), 3)
    }
}
