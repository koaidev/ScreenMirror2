package com.bangbangcoding.screenmirror.db.model.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "shortcut")
class Shortcut(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @Keep @ColumnInfo val name: String,
    @Keep @ColumnInfo val url: String,
    @Keep @ColumnInfo val canEdit: Boolean
)