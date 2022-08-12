package com.bangbangcoding.screenmirror.web.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

object DateTimeUtils {

    fun formatDateTime(millisecond: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millisecond
        val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return timeFormat.format(calendar.time)
    }

//    fun formatTime(millisecond: Long): String {
//        val calendar = Calendar.getInstance()
//        calendar.timeInMillis = millisecond
//        val timeFormat = SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault())
//        return timeFormat.format(calendar.time)
//    }

    fun formatTime(millisecond: Long): String? {
        val round = (millisecond / 100f).roundToInt() * 100L
        return String.format(
            Locale.getDefault(),
            "%d:%02d:%02d",
            round / 1000L / 60 / 60,
            (round - (1000L / 60 / 60)) / 1000L / 60,
            round / 1000L % 60
        )
    }
}