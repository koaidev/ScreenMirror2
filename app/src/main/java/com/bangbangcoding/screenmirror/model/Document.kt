package com.bangbangcoding.screenmirror.model

import android.net.Uri
import androidx.annotation.Keep

@Keep
data class DocumentItem(
    @Keep val id: Long,
    @Keep val title: String,
    @Keep val dateCreated: Long,
    @Keep val size: Int,
    @Keep val type: String,
    @Keep val uriDoc: Uri
) {
    constructor() : this(0, "", 0, 0, "", Uri.EMPTY)
}