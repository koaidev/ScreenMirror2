package com.bangbangcoding.screenmirror.web.ui.model.apis_models

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep

@Keep
class VideoInfo() : Parcelable {
    var commentCount: Long = 0
    var description: String? = null
    var displayID: String? = null
    var duration: String? = null
    var ext: String? = null
    var extractor: String? = null
    var extractorKey: String? = null
    var format: String? = null
    var formatID: String? = null
    var formats: List<Format>? = null
    var height: Long = 0
    var width: Long = 0
    var httpHeaders: HTTPHeaders? = null
    var id: String? = null
    var playlist: Any? = null
    var playlistIndex: Any? = null
    var protocol: String? = null
    var thumbnail: String? = null
    var thumbnails: List<Thumbnail>? = null
    var timestamp = 0.0
    var title: String? = "null"
    var url: String? = "null"
    var size: Int = 0
    var fileSize : Long = 0L
    var isSelect = false

    constructor(parcel: Parcel) : this() {
        commentCount = parcel.readLong()
        description = parcel.readString()
        displayID = parcel.readString()
        duration = parcel.readString()
        ext = parcel.readString()
        extractor = parcel.readString()
        extractorKey = parcel.readString()
        format = parcel.readString()
        formatID = parcel.readString()
        height = parcel.readLong()
        width = parcel.readLong()
        id = parcel.readString()
        protocol = parcel.readString()
        thumbnail = parcel.readString()
        timestamp = parcel.readDouble()
        title = parcel.readString()
        url = parcel.readString()
        size = parcel.readInt()
        fileSize = parcel.readLong()
        isSelect = parcel.readByte() != 0.toByte()
    }

    fun getQualityVideo() : String {
        return this.format?.let {
            if (it.contains(" ") || it.contains("-")) {
                if (!formatID.isNullOrEmpty() && !it.contains("-")) {
                    formatID
                } else if (height == 0L) {
                    "HD"
                } else {
                    this.height.toString() + "p"
                }
            } else {
                it
            }
        } ?: let {
            if (height == 0L) {
                "HD"
            } else {
                this.height.toString() + "p"
            }
        }
    }

//    private fun getFormat(): String? {
//        if(format == null) return null
//
//        if (format!!.contains(Regex("^[shdSHD]+"))) {
//
//        }
//
//        val pattern = Pattern.compile("\\d+p")
//        val matcher = pattern.matcher(link)
//        val formats: MutableList<String> = ArrayList()
//        while (matcher.find()) {
//            val data = link.substring(matcher.start(), matcher.end())
//            formats.add(data)
//            Log.e("ttt", "getFormat: $data")
//        }
//        return if (formats.isEmpty()) {
//            ""
//        } else formats[0]
//    }
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(commentCount)
        parcel.writeString(description)
        parcel.writeString(displayID)
        parcel.writeString(duration)
        parcel.writeString(ext)
        parcel.writeString(extractor)
        parcel.writeString(extractorKey)
        parcel.writeString(format)
        parcel.writeString(formatID)
        parcel.writeLong(height)
        parcel.writeLong(width)
        parcel.writeString(id)
        parcel.writeString(protocol)
        parcel.writeString(thumbnail)
        parcel.writeDouble(timestamp)
        parcel.writeString(title)
        parcel.writeString(url)
        parcel.writeInt(size)
        parcel.writeLong(fileSize)
        parcel.writeByte(if (isSelect) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VideoInfo> {
        override fun createFromParcel(parcel: Parcel): VideoInfo {
            return VideoInfo(parcel)
        }

        override fun newArray(size: Int): Array<VideoInfo?> {
            return arrayOfNulls(size)
        }
    }
}