package com.bangbangcoding.screenmirror.utils

object StringUtils {
    @JvmStatic
    fun isEmpty(str: String?): Boolean {
        return str == null || str.isEmpty()
    }

    @JvmStatic
    fun isNotEmpty(str: String?): Boolean {
        return !isEmpty(str)
    }

    fun isValidUrl(url: String): Boolean {
        if (isEmpty(url)) {
            return false
        }
        if (url.startsWith("http://")
            || url.startsWith("https://")
            || url.startsWith("ftp://")
            || url.startsWith("file://")
        ) {
            return true
        }
        return "about:blank" == url
    }
}