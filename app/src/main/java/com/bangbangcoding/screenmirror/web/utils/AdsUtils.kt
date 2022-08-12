package com.bangbangcoding.screenmirror.web.utils

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.size
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdsUtils {
    private val mInterstitialAd: InterstitialAd? = null

    fun addInterstitialAd(context: Context?) {
        val adRequest = AdRequest.Builder().build()
        if (context != null) {
            InterstitialAd.load(context, Constants.ADS_INTERSTITIAL_ID, adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
    //                        mInterstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded")
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        // Handle the error
                        Log.i("TAG", loadAdError.message)
                        //                        mInterstitialAd = null;
                    }
                })
        }
    }

    fun getAdSize(activity: Activity): AdSize {
//        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        val display = activity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density
        val adWidth = (widthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }


    fun loadBanner(activity: FragmentActivity, adContainerView: FrameLayout) {
        val adView = AdView(activity)
        adView.adUnitId = Constants.ADS_BANNER_ID
        adContainerView.addView(adView)

        // Create an ad request. Check your logcat output for the hashed device ID
        // to get test ads on a physical device, e.g.,
        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this
        // device."
        val adRequest = AdRequest.Builder().build()
        val adSize = getAdSize(activity)
        // Step 4 - Set the adaptive ad size on the ad view.

        // Step 5 - Start loading the ad in the background.
        adView.setAdSize(AdSize.BANNER)
        adView.loadAd(adRequest)
        adView.adListener = object : AdListener() {
            override fun onAdClosed() {
                super.onAdClosed()
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                adContainerView.visibility = View.GONE
            }

            override fun onAdOpened() {
                super.onAdOpened()
            }

            override fun onAdLoaded() {
                adContainerView.visibility = View.VISIBLE
            }

            override fun onAdClicked() {
                super.onAdClicked()
            }

            override fun onAdImpression() {
                super.onAdImpression()
            }
        }
    }

//    private fun getAdSize(activity: AppCompatActivity): AdSize? {
//        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
//        val display = activity.windowManager.defaultDisplay
//        val outMetrics = DisplayMetrics()
//        display.getMetrics(outMetrics)
//        val widthPixels = outMetrics.widthPixels.toFloat()
//        val density = outMetrics.density
//        val adWidth = (widthPixels / density).toInt()
//
//        // Step 3 - Get adaptive ad size and return for setting on the ad view.
//        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
//    }
//
//    private fun loadNativeAds(context: Context) {
//        val adLoader = AdLoader.Builder(context, Constants.ADS_NATIVE_ID)
//            .forNativeAd {
//                // Show the ad.
//            }
//            .withAdListener(object : AdListener() {
//                override fun onAdFailedToLoad(adError: LoadAdError) {
//                    // Handle the failure by logging, altering the UI, and so on.
//                }
//            })
//            .withNativeAdOptions(
//                NativeAdOptions.Builder() // Methods in the NativeAdOptions.Builder class can be
//                    // used here to specify individual options settings.
//                    .build()
//            )
//            .build()
//        adLoader.loadAd(AdRequest.Builder().build())
//
//
////        adLoader.loadAds(new AdRequest.Builder().build(), 3);
//    }
//
//
//    private fun loadNativeAdsTemplate(context: Context, viewGroup: ViewGroup) {
//        val adLoader = AdLoader.Builder(context, Constants.ADS_NATIVE_ID)
//            .forNativeAd { nativeAd ->
//                // Show the ad.
//                val styles = NativeTemplateStyle.Builder().build()
//                val template: TemplateView = viewGroup.findViewById(R.id.my_template)
//                template.setStyles(styles)
//                template.setNativeAd(nativeAd)
//
////                        if (isDestroyed()) {
////                            nativeAd.destroy();
////                            return;
////                        }
//            }
//            .withAdListener(object : AdListener() {
//                override fun onAdFailedToLoad(adError: LoadAdError) {
//                    val template: TemplateView = viewGroup.findViewById(R.id.my_template)
//                    template.visibility = View.GONE
//                    // Handle the failure by logging, altering the UI, and so on.
//                }
//            })
//            .withNativeAdOptions(
//                NativeAdOptions.Builder() // Methods in the NativeAdOptions.Builder class can be
//                    // used here to specify individual options settings.
//                    .build()
//            )
//            .build()
//        adLoader.loadAd(AdRequest.Builder().build())
//    }
}