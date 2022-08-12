package com.bangbangcoding.screenmirror.web.data.local.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by ThaoBKN on 28/12/2021
 */

data class AudioDownloadModel constructor(
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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readLong()
    ) {
    }

    fun getUri(): String {
        return "content://media/external/video/media/$id"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(link)
        parcel.writeString(path)
        parcel.writeString(thumbnail)
        parcel.writeLong(duration)
        parcel.writeString(size)
        parcel.writeInt(width)
        parcel.writeInt(height)
        parcel.writeLong(createDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AudioDownloadModel> {
        override fun createFromParcel(parcel: Parcel): AudioDownloadModel {
            return AudioDownloadModel(parcel)
        }

        override fun newArray(size: Int): Array<AudioDownloadModel?> {
            return arrayOfNulls(size)
        }
    }
}