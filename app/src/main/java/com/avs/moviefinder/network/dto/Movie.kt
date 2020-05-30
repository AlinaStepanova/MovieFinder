package com.avs.moviefinder.network.dto

import com.google.gson.annotations.SerializedName

data class Movie(
    val id: Int = 0,
    val title: String = "",
    val overview: String = "",
    @SerializedName("poster_path")
    val posterPath: String = "",
    @SerializedName("release_date")
    val year: String = "1920"
)