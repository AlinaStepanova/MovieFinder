package com.avs.moviefinder.data.dto

import android.os.Parcel
import android.os.Parcelable

data class Cast(
    var id: Int = 0,
    var known_for_department: String? = "",
    var name: String? = "",
    var popularity: Double = 0.0,
    var profile_path: String? = "",
    var cast_id: Int = 0,
    var character: String? = "",
    var credit_id: String? = "",
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(known_for_department)
        parcel.writeString(name)
        parcel.writeDouble(popularity)
        parcel.writeString(profile_path)
        parcel.writeInt(cast_id)
        parcel.writeString(character)
        parcel.writeString(credit_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Cast> {
        override fun createFromParcel(parcel: Parcel): Cast {
            return Cast(parcel)
        }

        override fun newArray(size: Int): Array<Cast?> {
            return arrayOfNulls(size)
        }
    }
}

data class Crew(
    var id: Int = 0,
    var name: String? = "",
    var popularity: Double = 0.0,
    var profile_path: String? = "",
    var credit_id: String? = "",
    var department: String? = "",
    var job: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeDouble(popularity)
        parcel.writeString(profile_path)
        parcel.writeString(credit_id)
        parcel.writeString(department)
        parcel.writeString(job)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Crew> {
        override fun createFromParcel(parcel: Parcel): Crew {
            return Crew(parcel)
        }

        override fun newArray(size: Int): Array<Crew?> {
            return arrayOfNulls(size)
        }
    }
}