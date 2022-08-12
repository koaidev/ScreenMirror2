package com.bangbangcoding.screenmirror.web.data.remote.response

import com.google.gson.annotations.SerializedName

class BaseResponse {

    @SerializedName("success")
    val success: Boolean = false
}