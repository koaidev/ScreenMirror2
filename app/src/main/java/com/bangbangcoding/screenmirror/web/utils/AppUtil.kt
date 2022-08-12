package com.bangbangcoding.screenmirror.web.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

object AppUtil {

    fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun hideSoftKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun convertDuration(dura: Int): String? {
        val sec = dura / 1000 % 60
        val min = dura / 1000 / 60 % 60
        val hour = dura / 1000 / 60 / 60
        var s = ""
        var m = ""
        var h = ""
        s = if (sec < 10) {
            "0$sec"
        } else {
            sec.toString() + ""
        }
        m = if (min < 10) {
            "0$min"
        } else {
            min.toString() + ""
        }
        h = if (hour < 10) {
            "0$hour"
        } else {
            hour.toString() + ""
        }
        var duration = ""
        duration = if (hour == 0) {
            "$m:$s"
        } else {
            "$h:$m:$s"
        }
        return duration
    }

    fun getDurationRewind(dura: Long): Long {
        if (dura / 1000/ 60 < 1) return 3000
        if (dura / 1000/ 60 < 3) return 5000
        return 10000
    }
}