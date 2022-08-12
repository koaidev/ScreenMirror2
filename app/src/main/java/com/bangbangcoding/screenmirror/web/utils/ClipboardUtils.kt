package com.bangbangcoding.screenmirror.web.utils

import android.content.ClipboardManager
import android.content.Context
import android.util.Log


object ClipboardUtils {

    fun checkHasCopy(context: Context): String? {
        var clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        clipboard.primaryClip?.let {
            try {
                val pasteData = it.getItemAt(0).text
                if (pasteData != null && UrlUtils.checkURL(pasteData)) {
                    return pasteData.toString()
                }else{
                    return null
                }
            }catch (e:Exception){
                Log.e("ClipboardUtils", e.toString())
            }

        } ?: return null

        return null
    }
}