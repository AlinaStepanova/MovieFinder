package com.avs.moviefinder.data.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Genre(var name: String? = "") : Parcelable