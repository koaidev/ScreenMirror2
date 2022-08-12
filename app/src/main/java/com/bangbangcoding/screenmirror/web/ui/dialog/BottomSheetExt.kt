package com.bangbangcoding.screenmirror.web.ui.dialog

import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.bangbangcoding.screenmirror.R


fun Context.showBottomSheet() {
    val bottomSheetDialog = BottomSheetDialog(this)
    bottomSheetDialog.setContentView(R.layout.bottomsheet_quality_list)

//    val copy = bottomSheetDialog.findViewById<LinearLayout>(R.id.copyLinearLayout)
//    val share = bottomSheetDialog.findViewById<LinearLayout>(R.id.shareLinearLayout)
//    val upload = bottomSheetDialog.findViewById<LinearLayout>(R.id.uploadLinearLayout)
//    val download = bottomSheetDialog.findViewById<LinearLayout>(R.id.download)
//    val delete = bottomSheetDialog.findViewById<LinearLayout>(R.id.delete)

    bottomSheetDialog.show()
}


