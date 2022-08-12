package com.bangbangcoding.screenmirror.web.utils

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

fun String.toSHA256String(): String {
    return try {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(this.toByteArray())
        val toRet = java.lang.StringBuilder()
        for (i in digest.indices) {
            val b: Int = digest[i].toInt() and 0xff // .and(0xff.toByte()).toInt()
            val hex = Integer.toHexString(b)
            if (hex.length == 1) toRet.append("0")
            toRet.append(hex)
        }
        return toRet.toString()
    } catch (e: NoSuchAlgorithmException) {
        ""
    }
}