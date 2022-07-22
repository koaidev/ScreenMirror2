package com.bangbangcoding.screenmirror.model

import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.Keep


@Keep
data class MediaItem(
    @Keep val id: Long,
    @Keep val isVideo: Boolean,
    @Keep val uri: Uri,
    @Keep val name: String,
    @Keep val duration: Int?,
    @Keep val size: Int?
)
