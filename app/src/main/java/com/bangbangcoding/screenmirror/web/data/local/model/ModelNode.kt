package com.bangbangcoding.screenmirror.web.data.local.model

import com.google.gson.annotations.SerializedName

class ModelNode {

    @SerializedName("display_url")
    val displayUrl: String? = null

    @SerializedName("display_resources")
    val displayResources: List<ModelDispRes>? = null

    @SerializedName("is_video")
    val isVideo = false

    @SerializedName("video_url")
    val videoUrl: String? = null

}