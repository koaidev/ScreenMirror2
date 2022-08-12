package com.bangbangcoding.screenmirror.web.data.local.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.bangbangcoding.screenmirror.web.data.local.room.entity.VideoInfo


data class VideoInfoWrapper constructor(
    @SerializedName("info")
    @Expose
    var videoInfo: VideoInfo?
)