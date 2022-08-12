package com.bangbangcoding.screenmirror.web.ui.feature.select_path

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.databinding.FragmentPathDownloadBinding
import com.bangbangcoding.screenmirror.web.ui.base.BaseFragment
import javax.inject.Inject

class PathDownloadFragment : BaseFragment() {

    companion object {
        fun newInstance() = PathDownloadFragment()
    }


    private var _binding: FragmentPathDownloadBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PathDownloadViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this, viewModelFactory)[PathDownloadViewModel::class.java]

        _binding = FragmentPathDownloadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupUI() {
        binding.toolbarLayout.tvTitle.text = getString(R.string.text_select_path_download)
        binding.toolbarLayout.ivBackToolbar.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

}