package com.bangbangcoding.screenmirror.web.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bangbangcoding.screenmirror.web.utils.FilesUtils.getFileSize
import com.bangbangcoding.screenmirror.web.utils.RoomConverter
import java.util.*

@Entity(tableName = "ProgressInfo")
@TypeConverters(RoomConverter::class)
data class ProgressInfo constructor(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),

    var downloadId: Long = 0,

    @TypeConverters(RoomConverter::class)
    var videoInfo: VideoInfo,

    var bytesDownloaded: Int = 0,

    var bytesTotal: Int = 0
) {

    var progress: Int = 0
        get() = (bytesDownloaded * 100f / bytesTotal).toInt()

    var progressSize: String = ""
        get() = getFileSize(bytesDownloaded.toDouble()) + "/" + getFileSize(bytesTotal.toDouble())

}