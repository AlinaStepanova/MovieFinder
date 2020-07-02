package com.avs.moviefinder.network.dto

import com.google.gson.annotations.SerializedName

data class Movie(
    val id: Long = 0,
    @SerializedName("imdb_id")
    val imdbId: Long = 0,
    val title: String = "",
    val overview: String = "",
    val revenue: Double = 0.0,
    val budget: Double = 0.0,
    val runtime: Int = 0,
    val tagline: String = "",
    val homepage: String = "",
    val popularity: Double = 0.0,
    @SerializedName("vote_count")
    val voteCount: Int = 0,
    @SerializedName("genres")
    val genres: List<Genre> = ArrayList(),
    @SerializedName("poster_path")
    val posterPath: String = "",
    @SerializedName("release_date")
    val year: String = "",
    @SerializedName("vote_average")
    val rating: String = ""
)