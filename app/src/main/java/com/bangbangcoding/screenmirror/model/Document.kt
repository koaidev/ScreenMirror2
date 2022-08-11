package com.bangbangcoding.screenmirror.model

import android.net.Uri
import androidx.annotation.Keep
import com.bangbangcoding.screenmirror.adapter.ItemViewType

@Keep
data class DocumentItem(
    @Keep var id: Long?  = 0,
    @Keep var viewType: Int? = ItemViewType.TXT.value,
    @Keep val title: String? = null,
    @Keep val dateCreated: Long? = 0,
    @Keep val size: Int? = 0,
    @Keep val type: String? = null,
    @Keep val uriDoc: Uri? = null
)