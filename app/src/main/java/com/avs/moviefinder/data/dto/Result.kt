package com.avs.moviefinder.data.dto

import com.google.gson.annotations.SerializedName

data class Result(
    var id: String? = "",
    var key: String? = "",
    var name: String? = "",
    var site: String? = "",
    var size: Int = 0,
    var type: String? = "",
    @SerializedName("backdrop_path")
    var backdropPath: String? = "",
    @SerializedName("original_title")
    var originalTitle: String? = "",
    var overview: String? = "",
    @SerializedName("poster_path")
    var posterPath: String? = "",
    @SerializedName("release_date")
    var releaseDate: String? = "",
    var title: String? = "",
    var video: Boolean = false,
    @SerializedName("vote_average")
    var voteAverage: Double = 0.0,
    @SerializedName("vote_count")
    var voteCount: Int = 0,
    var popularity: Double = 0.0,
)

fun Result.toMovie(): Movie {
    return Movie(
        id = id?.toLongOrNull() ?: 0,
        title = title,
        overview = overview,
        posterPath = posterPath,
        releaseDate = releaseDate,
        homepage = site,
        popularity = popularity,
        rating = voteAverage.toString(),
    )
}