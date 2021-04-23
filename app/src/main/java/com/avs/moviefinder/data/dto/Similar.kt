package com.avs.moviefinder.data.dto

import android.os.Parcel
import android.os.Parcelable

data class Similar (
    var page: Int = 0,
    var results: List<Result>? = ArrayList(),
    var total_pages: Int = 0,
    var total_results: Int = 0,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.createTypedArrayList(Result),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(page)
        parcel.writeTypedList(results)
        parcel.writeInt(total_pages)
        parcel.writeInt(total_results)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Similar> {
        override fun createFromParcel(parcel: Parcel): Similar {
            return Similar(parcel)
        }

        override fun newArray(size: Int): Array<Similar?> {
            return arrayOfNulls(size)
        }
    }
}