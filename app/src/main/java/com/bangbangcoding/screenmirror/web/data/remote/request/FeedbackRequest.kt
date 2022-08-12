package com.bangbangcoding.screenmirror.web.data.remote.request

import com.google.gson.annotations.SerializedName

data class FeedbackRequest(
    @SerializedName("application_id")
    val appId: String = "",

    @SerializedName("url")
    val url: String = "",

    @SerializedName("type")
    val type: String = "",

    @SerializedName("timestamp")
    val timestamp: Long = 0L,

    @SerializedName("checksum")
    val checksum: String = "",

    @SerializedName("app_version")
    val appVer: String = "",

    @SerializedName("auto_push")
    val autoPush: Boolean = false,
) : BaseRequest {
    override fun toString(): String {
        return "\nappId = $appId \nurl = $url \n" +
                "type = $type \n timestamp = $timestamp \n checkSum = $checksum" +
                "appVer = $appVer autoPush = $autoPush"
    }
}

enum class ErrorType(private val string: String) {
    ERROR_BROWSER_VIDEOS("ERROR_BROWSER_VIDEOS"),
    ERROR_DOWNLOAD_RESOURCES("ERROR_DOWNLOAD_RESOURCES")
}