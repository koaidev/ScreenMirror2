package com.bangbangcoding.screenmirror.db.model.group

import android.net.Uri
import androidx.annotation.Keep

open class ListItem(
    val type: Int
) {
    companion object {
        const val TYPE_DATE = 0
        const val TYPE_GENERAL = 1
    }
}

class GeneralItem(
    @Keep val id: Long,
    @Keep val isVideo: Boolean,
    @Keep val uri: Uri,
    @Keep val name: String,
    @Keep val duration: Int?,
    @Keep val size: Int?,
) : ListItem(TYPE_GENERAL)

class DateItem(
    val dateAdd: String
) : ListItem(TYPE_DATE)