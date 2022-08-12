package com.bangbangcoding.screenmirror.web.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "VideoInfo")
data class TabInfo constructor(
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

    @ColumnInfo(name = "originalUrl")
    var originalUrl: String = "",

    @ColumnInfo(name = "is_selected")
    var isSelected: Boolean = false
)
