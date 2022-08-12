package com.bangbangcoding.screenmirror.web.ui.feature.privacy

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.databinding.FragmentPrivacyBinding
import com.bangbangcoding.screenmirror.web.ui.base.BaseFragment

class PrivacyFragment : BaseFragment() {

    private var _binding: FragmentPrivacyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrivacyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupUI() {
        binding.toolbarLayout.tvTitle.text = getString(R.string.text_privacy)
        binding.toolbarLayout.ivBackToolbar.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.webView.webChromeClient = browserWebChromeClient

        binding.webView.loadUrl("https://sites.google.com/view/highsecureapps/trang-ch%E1%BB%A7")
    }

    private val browserWebChromeClient = object : WebChromeClient() {
        override fun onReceivedTitle(view: WebView?, title: String?) {
            Log.e("ttt--", "onReceivedTitle: ${view?.url};; $title")
            super.onReceivedTitle(view, title)
        }

        override fun onProgressChanged(view: WebView, newProgress: Int) {
            Log.e("ttt--", "onProgressChanged: ${view.url} ;; $newProgress")
            binding.progressBar.progress = newProgress
            binding.progressBar.visibility = if (newProgress == 100) View.GONE else View.VISIBLE
            super.onProgressChanged(view, newProgress)
        }
    }
}
