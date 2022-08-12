package com.bangbangcoding.screenmirror.web.data.local.model

import android.os.Parcel
import android.os.Parcelable
import com.bangbangcoding.screenmirror.R

data class DomainAllow(
    var id: Int = 0,
    var domain: String? = "",
    var payload: Payload?,
    var canEdit: Boolean? = true
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readParcelable(Payload::class.java.classLoader),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(domain)
        parcel.writeParcelable(payload, flags)
        parcel.writeByte(if (canEdit == true) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DomainAllow> {
        override fun createFromParcel(parcel: Parcel): DomainAllow {
            return DomainAllow(parcel)
        }

        override fun newArray(size: Int): Array<DomainAllow?> {
            return arrayOfNulls(size)
        }
    }
}

data class Payload(
    var name: String = "",
    var icon: String = "",
    var iconRes: Int = R.drawable.logo
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(icon)
        parcel.writeInt(iconRes)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Payload> {
        override fun createFromParcel(parcel: Parcel): Payload {
            return Payload(parcel)
        }

        override fun newArray(size: Int): Array<Payload?> {
            return arrayOfNulls(size)
        }
    }
}