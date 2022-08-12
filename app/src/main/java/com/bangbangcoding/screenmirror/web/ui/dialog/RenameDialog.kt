package com.bangbangcoding.screenmirror.web.ui.dialog

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.databinding.DialogRenameBinding
import com.bangbangcoding.screenmirror.web.utils.Constants
import java.io.File

class RenameDialog : DialogFragment() {

    private lateinit var binding: DialogRenameBinding

    var listener: OnRenameListener? = null
    private var mName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    companion object {
        private const val KEY_VIDEO = "KEY_VIDEO"

        fun newInstance(videoName: String): RenameDialog {
            val args = Bundle()
            args.putString(KEY_VIDEO, videoName)
            val fragment = RenameDialog()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogRenameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mName = arguments?.getString(KEY_VIDEO) ?: ""
        binding.btnConfirm.setOnClickListener {
            if (binding.edtText.text.toString().trim().isEmpty()) {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = getString(R.string.error_name)
            } else if (mName == binding.edtText.text.toString().trim()) {
                dismiss()
            } else {
                listener?.onRename(binding.edtText.text.toString().trim())
                dismiss()
            }
        }
        binding.btnCancel.setOnClickListener { dismiss() }
        binding.edtText.setText(mName)

        binding.edtText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("Not yet implemented")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("Not yet implemented")
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().trim().isNotEmpty()) {
                    val name = p0.toString().trim()
                    if (checkExists(name)) {
                        binding.btnConfirm.isEnabled = false
                        binding.tvError.visibility = View.VISIBLE
                        binding.tvError.text = getString(R.string.txt_error_video_exist)
                    } else {
                        binding.btnConfirm.isEnabled = true
                        binding.tvError.visibility = View.GONE
                    }
                } else {
                    binding.btnConfirm.isEnabled = false
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvError.text = getString(R.string.error_name)
                }
            }
        })
    }

    private fun checkExists(name: String): Boolean {
        val path = Constants.getExternalFile().absolutePath + "/$name.mp4"
        val fileTo = File(path)
        return fileTo.exists()
    }

    interface OnRenameListener {
        fun onRename(newName: String)
    }
}
