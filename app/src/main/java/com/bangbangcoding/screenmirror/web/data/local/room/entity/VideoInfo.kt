package com.bangbangcoding.screenmirror.web.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "VideoInfo")
data class VideoInfo constructor(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "downloadUrl")
    @SerializedName("url")
    @Expose
    var downloadUrl: String = "",

    @ColumnInfo(name = "title")
    @SerializedName("title")
    @Expose
    var title: String = "",

    @ColumnInfo(name = "ext")
    @SerializedName("ext")
    @Expose
    var ext: String = "",

    @ColumnInfo(name = "thumbnail")
    @SerializedName("thumbnail")
    @Expose
    var thumbnail: String ?= "",

    @ColumnInfo(name = "duration")
    @SerializedName("duration")
    @Expose
    var duration: Int = 0,

    @ColumnInfo(name = "originalUrl")
    var originalUrl: String = ""
) {

    val name
        get() = "$title.$ext"
}