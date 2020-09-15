package com.avs.moviefinder.data.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "movie_table")
data class Movie(
    @PrimaryKey
    var id: Long = 0,
    var title: String = "",
    var overview: String = "",
    @SerializedName("poster_path")
    @ColumnInfo(name = "poster_path")
    var posterPath: String = "",
    @SerializedName("release_date")
    @ColumnInfo(name = "release_date")
    var releaseDate: String = "",
    @SerializedName("vote_average")
    var rating: String = "",
    @SerializedName("imdb_id")
    @ColumnInfo(name = "imdb_id")
    var imdbId: String = "",
    var revenue: Double = 0.0,
    var budget: Double = 0.0,
    var runtime: Int = 0,
    var popularity: Double = 0.0,
    var tagline: String = "",
    var homepage: String = "",
    @SerializedName("vote_count")
    @ColumnInfo(name = "vote_count")
    var voteCount: Int = 0,
    @SerializedName("genres")
    var genres: List<Genre> = ArrayList(),
    var isFavorite: Boolean = false,
    var isInWatchLater: Boolean = false,
)