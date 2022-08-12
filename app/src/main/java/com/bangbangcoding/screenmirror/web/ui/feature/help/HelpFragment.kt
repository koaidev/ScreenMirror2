package com.bangbangcoding.screenmirror.web.ui.feature.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bangbangcoding.screenmirror.BuildConfig
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.databinding.FragmentHelpBinding
import com.bangbangcoding.screenmirror.web.ui.base.BaseFragment
import com.bangbangcoding.screenmirror.web.ui.feature.feedback.FeedbackFragment
import com.bangbangcoding.screenmirror.web.ui.feature.privacy.PrivacyFragment
import com.bangbangcoding.screenmirror.web.utils.IntentActivity
import javax.inject.Inject

class HelpFragment : BaseFragment() {

    companion object {
        fun newInstance() = HelpFragment()
    }

    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HelpViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this, viewModelFactory)[HelpViewModel::class.java]

        _binding = FragmentHelpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupUI() {
        binding.toolbarLayout.tvTitle.text = getString(R.string.txt_help)
        binding.toolbarLayout.ivBackToolbar.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.tvFeedback.setOnClickListener {
            startActivity(
                IntentActivity.getIntent(requireContext(), FeedbackFragment::class.java)
            )
        }

        binding.tvPrivacy.setOnClickListener {
            startActivity(
                IntentActivity.getIntent(requireContext(), PrivacyFragment::class.java)
            )
        }

        binding.tvVersion.text = getString(R.string.txt_version, BuildConfig.VERSION_NAME)
    }
}