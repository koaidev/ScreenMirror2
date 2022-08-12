package com.bangbangcoding.screenmirror.web.ui.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.bangbangcoding.screenmirror.databinding.DialogAdsEffectBinding
import com.bangbangcoding.screenmirror.web.utils.Constants

class AdsDialog : DialogFragment() {

    private lateinit var binding: DialogAdsEffectBinding

    //    var listener: OnRenameListener? = null
//
    var dismissListener: DismissListener? = null

    private var url: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onResume() {
        val lp = WindowManager.LayoutParams()
        val window = dialog!!.window ?: return
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog!!.window!!.attributes = lp
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        super.onResume()
    }

//    companion object {
//        private const val KEY_VIDEO = "KEY_VIDEO"
//        private const val KEY_BUTTON_FEEDBACK = "KEY_BUTTON_FEEDBACK"
//
//        fun newInstance(page: String = ""): AdsDialog {
//            val args = Bundle().apply {
//                putString(KEY_BUTTON_FEEDBACK, page)
//            }
//            val fragment = AdsDialog()
//            fragment.arguments = args
//            return fragment
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAdsEffectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadNativeAds()
        binding.ivClose.setOnClickListener {
            dismiss()
        }
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
                    binding.myTemplate.visibility = View.GONE
                    // Handle the failure by logging, altering the UI, and so on.
//                    Toast.makeText(
//                        requireContext(), "Failed to load native ad with error ${adError.message}",
//                        Toast.LENGTH_SHORT
//                    ).show()
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    // Methods in the NativeAdOptions.Builder class can be
                    // used here to specify individual options settings.
                    .build()
            )
            .build()

        adLoader.loadAds(AdRequest.Builder().build(), 3)
    }

    override fun onDismiss(dialog: DialogInterface) {
        dismissListener?.onDismiss()
        super.onDismiss(dialog)
    }

    interface DismissListener {
        fun onDismiss()
    }
}