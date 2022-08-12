package com.bangbangcoding.screenmirror.web.utils

import java.text.DecimalFormat

object FilesUtils {

    fun getFileSize(length: Double): String {
        val KiB = 1024
        val MiB = 1024 * 1024
        val decimalFormat = DecimalFormat("#.##")
        return when {
            length > MiB -> decimalFormat.format(length / MiB) + " MB"
            length > KiB -> decimalFormat.format(length / KiB) + " KB"
            else -> decimalFormat.format(length) + " B"
        }
    }
}