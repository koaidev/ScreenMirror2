package com.bangbangcoding.screenmirror.web.ui.paste.fragment

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.databinding.FragmentBuzzPasteLinkBinding
import com.bangbangcoding.screenmirror.web.ui.CustomToast
import com.bangbangcoding.screenmirror.web.ui.base.BaseFragmentDownload
import com.bangbangcoding.screenmirror.web.utils.Constants
import com.bangbangcoding.screenmirror.web.utils.Utils
import com.bangbangcoding.screenmirror.web.utils.ext.setColorSpannable
import com.bangbangcoding.screenmirror.web.utils.ext.snackbar


class BuzzPasteLinkFragment : BaseFragmentDownload() {
    private var _binding: FragmentBuzzPasteLinkBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuzzPasteLinkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupUI() {
        //Toolbar
        binding.toolbarLayout.tvTitle.setText(R.string.txt_buzz_video)
        binding.toolbarLayout2.tvTitle.setText(R.string.txt_buzz_video)
        binding.toolbarLayout.ivAction.setImageResource(R.drawable.ic_buzz_video_120)
        binding.toolbarLayout2.ivAction.setImageResource(R.drawable.ic_buzz_video_120)
        binding.toolbarLayout.ivAction.setOnClickListener {
            Utils.openApp(requireContext(), Constants.LinkOpen.TOP_BUZZ_STORE)
        }
        binding.toolbarLayout.ivBackToolbar.setOnClickListener {
            requireActivity().onBackPressed()
        }

        //
        binding.tvOpenLink.text =
            getString(R.string.text_open_and, getString(R.string.txt_buzz_video))
                .setColorSpannable()
        binding.tvOpenDownload.text =
            getString(R.string.text_open_and, getString(R.string.app_name))
                .setColorSpannable()

        binding.btnGoToWeb.setOnClickListener {
            goToWebsite()
        }

        binding.btnPaste.setOnClickListener {
            pasteLink()
        }
    }

    private fun goToWebsite() {
        val resultIntent = Intent()
        resultIntent.putExtra(Constants.KEY_GO_TO_WEB, Constants.LinkOpen.TOP_BUZZ)
        requireActivity().setResult(Activity.RESULT_OK, resultIntent)
        requireActivity().finish()
    }

    private fun pasteLink() {

        val clipBoardManager =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val primaryClipData = clipBoardManager.primaryClip
        val link = primaryClipData?.getItemAt(0)?.text?.toString() ?: ""

        if (link.isEmpty()) {
            CustomToast.makeText(requireContext(), getString(R.string.text_enter_link),
                CustomToast.SHORT, CustomToast.WARNING).show()
            return
        } else {
            binding.edtLink.setText(link)

        }
//        url.contains("topbuzz.com") || url.contains("buzzvideos.com")
        downloadVideo(link, "topbuzz.com", "buzzvideo.com", "buzzvideo") { hasData, _, videos ->
            Log.e("ttt", "Download PASTE_LINK: $hasData")
            if (hasData) {
                showBottomSheetDownloadVideos(videos.take(5).distinctBy { v -> v.format })
            } else {
                requireActivity().snackbar(R.string.error_no_found_video)
            }
        }
    }
}
