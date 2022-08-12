package com.bangbangcoding.screenmirror.web.data.local.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.text.format.Formatter
import androidx.room.ColumnInfo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.File

/**
 * Created by ThaoBKN on 28/12/2021
 */

data class VideoDownloadModel constructor(
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @SerializedName("title")
    @Expose
    var title: String = "",

    @SerializedName("link")
    @Expose
    var link: String = "",

    @SerializedName("path")
    @Expose
    var path: String = "",

    @SerializedName("display")
    @Expose
    var display: String = "",

    @SerializedName("thumbnail")
    @Expose
    var thumbnail: String = "",

    @SerializedName("duration")
    @Expose
    var duration: Long = 0L,

    @SerializedName("size")
    @Expose
    var size: String = "",

    @SerializedName("width")
    @Expose
    var width: Int = 0,

    @SerializedName("height")
    @Expose
    var height: Int = 0,

    @SerializedName("create_date")
    @Expose
    var createDate: Long = 0L,

    @SerializedName("show_checkbox")
    @Expose
    var isShow: Boolean = false,

    @SerializedName("checked")
    @Expose
    var checked: Boolean = false,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readLong(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    fun getUri(): String {
        return "content://media/external/video/media/$id"
    }

    fun getSizeVideo(context: Context) : String {
        return Formatter.formatFileSize(
            context,
            File(path).length()
        )
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(link)
        parcel.writeString(path)
        parcel.writeString(display)
        parcel.writeString(thumbnail)
        parcel.writeLong(duration)
        parcel.writeString(size)
        parcel.writeInt(width)
        parcel.writeInt(height)
        parcel.writeLong(createDate)
        parcel.writeByte(if (isShow) 1 else 0)
        parcel.writeByte(if (checked) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VideoDownloadModel> {
        override fun createFromParcel(parcel: Parcel): VideoDownloadModel {
            return VideoDownloadModel(parcel)
        }

        override fun newArray(size: Int): Array<VideoDownloadModel?> {
            return arrayOfNulls(size)
        }
    }
}