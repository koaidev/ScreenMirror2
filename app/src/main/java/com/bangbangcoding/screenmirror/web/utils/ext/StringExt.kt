package com.bangbangcoding.screenmirror.web.utils.ext

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorInt

fun String.setColorSpannable(
    first: Int,
    end: Int = this.length,
    @ColorInt color: Int = Color.parseColor("#037CF9")
): SpannableString {
    val spannableContent = SpannableString(this)

    spannableContent.setSpan(
        ForegroundColorSpan(color), first, end,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannableContent
}

fun String.setColorSpannable(
    firstRegex: Char = ' ',
    @ColorInt color: Int = Color.parseColor("#037CF9")
): SpannableString {
    val spannableContent = SpannableString(this)
    val end = this.indexOfFirst { it == firstRegex }
    if(end < 0){
        return spannableContent
    }
    spannableContent.setSpan(
        ForegroundColorSpan(color), 0, end,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannableContent
}