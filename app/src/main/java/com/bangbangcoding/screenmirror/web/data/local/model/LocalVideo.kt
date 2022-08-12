package com.bangbangcoding.screenmirror.web.data.local.model

import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.provider.MediaStore.Video.Thumbnails.MINI_KIND
import com.bangbangcoding.screenmirror.web.utils.FilesUtils.getFileSize
import java.io.File

data class LocalVideo constructor(
    var file: File
) {

    var size: String = ""
        get() = getFileSize(file.length().toDouble())

    val thumbnail: Bitmap?
        get() = ThumbnailUtils.createVideoThumbnail(file.path, MINI_KIND)

}