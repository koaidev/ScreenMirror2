package com.bangbangcoding.screenmirror.web.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.bangbangcoding.screenmirror.R
import java.util.UUID

@Entity(tableName = "PageInfo")
data class PageInfo constructor(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "name")
    @SerializedName("name")
    @Expose
    var name: String = "",

    @ColumnInfo(name = "link")
    @SerializedName("link")
    @Expose
    var link: String = "",

    @ColumnInfo(name = "icon")
    @SerializedName("icon")
    @Expose
    var icon: String = "",

    @ColumnInfo(name = "iconRes")
    @SerializedName("iconRes")
    @Expose
    var iconRes: Int = R.drawable.ic_downloader_logo_128
)