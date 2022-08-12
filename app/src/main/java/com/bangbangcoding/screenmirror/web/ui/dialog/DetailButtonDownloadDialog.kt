package com.bangbangcoding.screenmirror.web.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.databinding.DialogContentButtonDownloadBinding
import com.bangbangcoding.screenmirror.web.ui.feature.feedback.FeedbackFragment
import com.bangbangcoding.screenmirror.web.ui.feature.feedback.FeedbackFragment.Companion.KEY_PAGE_FEEDBACK
import com.bangbangcoding.screenmirror.web.utils.IntentActivity
import com.bangbangcoding.screenmirror.web.utils.ext.setColorSpannable

class DetailButtonDownloadDialog : DialogFragment() {

    private lateinit var binding: DialogContentButtonDownloadBinding

    var listener: OnRenameListener? = null

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

    companion object {
        private const val KEY_VIDEO = "KEY_VIDEO"
        private const val KEY_BUTTON_FEEDBACK = "KEY_BUTTON_FEEDBACK"

        fun newInstance(page: String = ""): DetailButtonDownloadDialog {
            val args = Bundle().apply {
                putString(KEY_BUTTON_FEEDBACK, page)
            }
            val fragment = DetailButtonDownloadDialog()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogContentButtonDownloadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name = arguments?.getString(KEY_VIDEO) ?: ""
        url = arguments?.getString(KEY_BUTTON_FEEDBACK) ?: ""
        binding.tvDesc.text = getString(R.string.text_desc_download)
            .setColorSpannable(',')
        binding.tvGotIt.setOnClickListener {
            dismissListener?.onDismissNoResource(url)
            dismiss()
        }
        binding.tvGetFeedback.setOnClickListener {
            startActivity(
                IntentActivity.getIntent(requireContext(), FeedbackFragment::class.java,
                    args = Bundle().apply {
                        putString(KEY_PAGE_FEEDBACK, url)
                    })
            )
            dismiss()
        }
    }

    interface OnRenameListener {
        fun onRename(newName: String)
    }

    interface DismissListener {
        fun onDismissNoResource(url: String)
    }
}