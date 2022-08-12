package com.bangbangcoding.screenmirror.web.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.bangbangcoding.screenmirror.databinding.FragmentTestAdsBinding
import com.bangbangcoding.screenmirror.web.utils.Constants

class TestAdsFragment : Fragment() {


    private var mInterstitialAd: InterstitialAd? = null
    private var isShowAd = false
    private var mTypeAds = 0
    private lateinit var binding: FragmentTestAdsBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loadInterstitialAd(requireActivity())
        binding = FragmentTestAdsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.show.setOnClickListener {
            showInterstitial(0)
        }
    }


    private fun loadInterstitialAd(context: Context) {
        Log.e("ttt", "loadInterstitialAd: isTestDevice")
//        if (!BuildConfig.DEBUG) {
        val adRequest = AdRequest.Builder().build()
        if (adRequest.isTestDevice(context)) {
            Log.e("ttt", "loadInterstitialAd: true")
        }
        InterstitialAd.load(context, Constants.ADS_INTERSTITIAL_ID, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    mInterstitialAd = interstitialAd
                    Log.d("ttt", "mloadInterstitialAds onAdLoaded")
                    interstitialAd.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null
                                Log.w("ttt", "The ads was dismissed.")
                                binding.container.visibility = View.VISIBLE
                                if (isShowAd) {
                                    openScreen(mTypeAds)
                                }
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                // Called when fullscreen content failed to show.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null
                                Log.w("ttt", "The ads failed to show.")
                            }

                            override fun onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                Log.w("ttt", "The ads was shown.")
                                binding.container.visibility = View.GONE
                                isShowAd = true
                            }
                        }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    Log.w("ttt", loadAdError.message)
                    mInterstitialAd = null
                    if (isShowAd) {
                        openScreen(mTypeAds)
                    }
                }
            })
//        }
    }

    private fun showInterstitial(type: Int) {
        mTypeAds = type
        if (mInterstitialAd != null) {
            Log.e("ttt", "showInterstitial: ")
            mInterstitialAd!!.show(requireActivity())
        } else {
            Log.e("ttt", "showInterstitial: null")
            openScreen(type)
        }
    }

//    override fun onBackPressed() {
//        if (!isShowAd) {
//            showInterstitial(0)
//        } else {
//            super.onBackPressed()
//        }
//    }

    private fun openScreen(type: Int) {
        //Toast.makeText(requireContext(), "Reload", Toast.LENGTH_LONG).show()
//        finish()
    }
}
