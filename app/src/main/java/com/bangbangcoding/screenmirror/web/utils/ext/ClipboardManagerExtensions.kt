package com.bangbangcoding.screenmirror.web.utils.ext

import android.content.ClipData
import android.content.ClipboardManager

/**
 * Copies the [text] to the clipboard with the label `URL`.
 */
fun ClipboardManager.copyToClipboard(text: String) {
    this.setPrimaryClip(ClipData.newPlainText("URL", text))
}
