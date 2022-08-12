@file:Suppress("NOTHING_TO_INLINE")

package com.bangbangcoding.screenmirror.web.utils.ext

import androidx.appcompat.app.AlertDialog
import com.bangbangcoding.screenmirror.web.ui.dialog.BrowserDialog

/**
 * Ensures that the dialog is appropriately sized and displays it.
 */
inline fun AlertDialog.Builder.resizeAndShow() = BrowserDialog.setDialogSize(context, this.show())
