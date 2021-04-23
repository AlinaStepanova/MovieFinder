package com.avs.moviefinder.data.dto

import android.os.Parcel
import android.os.Parcelable

data class Videos (var results: List<Result>? = ArrayList()) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.createTypedArrayList(Result)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(results)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Videos> {
        override fun createFromParcel(parcel: Parcel): Videos {
            return Videos(parcel)
        }

        override fun newArray(size: Int): Array<Videos?> {
            return arrayOfNulls(size)
        }
    }
}