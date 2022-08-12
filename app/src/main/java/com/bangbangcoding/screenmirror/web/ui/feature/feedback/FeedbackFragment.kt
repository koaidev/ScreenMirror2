package com.bangbangcoding.screenmirror.web.ui.feature.feedback

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.web.data.remote.request.ErrorType
import com.bangbangcoding.screenmirror.databinding.FragmentFeedbackBinding
import com.bangbangcoding.screenmirror.web.ui.CustomToast
import com.bangbangcoding.screenmirror.web.ui.base.BaseFragment
import com.bangbangcoding.screenmirror.web.utils.Constants
import java.util.regex.Pattern
import javax.inject.Inject

class FeedbackFragment : BaseFragment() {

    companion object {
        const val KEY_PAGE_FEEDBACK = "KEY_PAGE_FEEDBACK"

        fun newInstance(page : String = "") = FeedbackFragment().apply {
            val args = Bundle().apply {
                putString(KEY_PAGE_FEEDBACK, page)
            }
            this.arguments = args
        }
    }

    private var _binding: FragmentFeedbackBinding? = null
    private val binding get() = _binding!!
    private var type = -1

    private lateinit var viewModel: FeedbackViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this, viewModelFactory)[FeedbackViewModel::class.java]

        _binding = FragmentFeedbackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupUI() {
        loadNativeAds()
        binding.toolbarLayout.tvTitle.text = getString(R.string.text_feedback)
        binding.toolbarLayout.ivBackToolbar.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btCancel.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.tvCantBrowse.setOnClickListener {
            binding.cbBrowse.isChecked = !binding.cbBrowse.isChecked
        }

        binding.tvNoDownload.setOnClickListener {
            binding.cbNoDownload.isChecked = !binding.cbNoDownload.isChecked
        }

        binding.tvTooManyAds.setOnClickListener {
            binding.cbTooManyAds.isChecked = !binding.cbTooManyAds.isChecked
        }

        binding.cbBrowse.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                type = 0
                binding.cbNoDownload.isChecked = false
                binding.cbTooManyAds.isChecked = false
            }
            binding.edtLinkBrowse.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        binding.cbNoDownload.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                type = 1
                binding.cbBrowse.isChecked = false
                binding.cbTooManyAds.isChecked = false
            }
            binding.edtLink.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
        binding.cbTooManyAds.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                type = 2
                binding.cbBrowse.isChecked = false
                binding.cbNoDownload.isChecked = false
            }
        }

        binding.btOk.setOnClickListener {
            handlePositive()
        }

        arguments?.let {
            val page = it.getString(KEY_PAGE_FEEDBACK) ?: ""
            binding.edtLink.setText(page)
            binding.edtLinkBrowse.setText(page)
        }
    }

    private fun handlePositive() {
        when (type) {
            0 -> {
                val url = binding.edtLinkBrowse.text.toString().trim()
                if (url.isNotEmpty()) {
                    val newUrl = checkValidUrl(url)
                    if (newUrl.isNotEmpty()) {
                        viewModel.addFeedback(
//                            BuildConfig.APPLICATION_ID,
                            "com.highsecure.videodownloader",
                            newUrl,
                            ErrorType.ERROR_BROWSER_VIDEOS.name
                        )
                    }
                } else {
                    CustomToast.makeText(requireContext(), getString(R.string.txt_please_the_website),CustomToast.SHORT, CustomToast.WARNING).show()
//                    Toast.makeText(
//                        requireContext(),
//                        getString(R.string.txt_please_the_website),
//                        Toast.LENGTH_SHORT
//                    ).show()
                }
            }
            1 -> {
                val url = binding.edtLink.text.toString().trim()
                if (url.isNotEmpty()) {
                    val newUrl = checkValidUrl(url)
                    if (newUrl.isNotEmpty()) {
                        viewModel.addFeedback(
                            "com.highsecure.videodownloader",
//                            BuildConfig.APPLICATION_ID,
                            newUrl,
                            ErrorType.ERROR_BROWSER_VIDEOS.name
                        )
                    }
                } else {
                    CustomToast.makeText(requireContext(), getString(R.string.txt_feedback_error),CustomToast.SHORT, CustomToast.WARNING).show()
//                    Toast.makeText(
//                        requireContext(),
//                        getString(R.string.txt_please_the_website),
//                        Toast.LENGTH_SHORT
//                    ).show()
                }
            }
            -1 -> {
                CustomToast.makeText(requireContext(), getString(R.string.txt_feedback_error),CustomToast.SHORT, CustomToast.WARNING).show()
//                Toast.makeText(
//                    requireContext(),
//                    getString(R.string.txt_please_choose_feedback),
//                    Toast.LENGTH_SHORT
//                ).show()
            }
            else -> {
                requireActivity().onBackPressed()
            }
        }
    }

    private fun loadNativeAds() {
        val adLoader = AdLoader.Builder(requireContext(), Constants.ADS_NATIVE_ID)
            .forNativeAd { ad : NativeAd ->
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
                // Methods in the NativeAdOptions.Builder class can be
                // used here to specify individual options settings.
                .build())
            .build()

        adLoader.loadAds(AdRequest.Builder().build(), 3)
    }

    private fun checkValidUrl(input: String, pattern: Pattern = Patterns.WEB_URL): String {
        if (input.startsWith("http://") || input.startsWith("https://")) {
            return input
        } else if (pattern.matcher(input).matches()) {
            return "http://$input"
        } else {
//            Toast.makeText(
//                requireContext(),
//                getString(R.string.invalid_url),
//                Toast.LENGTH_SHORT
//            ).show()
            CustomToast.makeText(requireContext(), getString(R.string.txt_feedback_error),CustomToast.SHORT, CustomToast.WARNING).show()
            return ""
        }
    }

    override fun observerViewModel() {
        viewModel.addFeedback.observe(viewLifecycleOwner, {
            it?.let {
                if (it){
                   // Toast.makeText(requireContext(), R.string.txt_feedback_ok, Toast.LENGTH_SHORT).show()
                    CustomToast.makeText(requireContext(), getString(R.string.txt_feedback_ok),CustomToast.SHORT, CustomToast.SUCCESS).show()
                }else{
                    //Toast.makeText(requireContext(), R.string.txt_feedback_error, Toast.LENGTH_SHORT).show()
                    CustomToast.makeText(requireContext(), getString(R.string.txt_feedback_error),CustomToast.SHORT, CustomToast.WARNING).show()
                }
                requireActivity().onBackPressed()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
