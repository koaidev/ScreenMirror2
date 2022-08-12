package com.bangbangcoding.screenmirror.web.ui.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.bangbangcoding.screenmirror.databinding.BottomsheetExitAppBinding

class ExitAppBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomsheetExitAppBinding? = null
    private val binding get() = _binding!!
    var mListener: OnDismissListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetExitAppBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivClose.setOnClickListener {
//            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        mListener?.onDismiss()
        super.onDismiss(dialog)
    }

    interface OnDismissListener {
        fun onDismiss()
    }
}
