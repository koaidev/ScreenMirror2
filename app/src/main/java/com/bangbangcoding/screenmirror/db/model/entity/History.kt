package com.bangbangcoding.screenmirror.db.model.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "history")
data class History(
    @Keep @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    var id: Int?,
    @Keep
    @ColumnInfo
    var title: String,
    @Keep
    @ColumnInfo
    var url: String,
    @Keep
    @ColumnInfo
    var time: Long = 0
) {
    constructor() : this(0, "", "", 0) {
    }

    constructor(
        title: String,
        url: String,
        time: Long
    ) : this(0, title,url,time)
}