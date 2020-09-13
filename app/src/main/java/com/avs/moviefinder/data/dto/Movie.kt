package com.avs.moviefinder.data.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.SerializedName

enum class BOOLEAN { FALSE, TRUE }

// inheritance of data classes is not supported
// see: https://stackoverflow.com/questions/26444145/extend-data-class-in-kotlin
@Entity(tableName = "movie_table")
open class Movie(
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
) : BaseMovie() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Movie) return false
        if (!super.equals(other)) return false

        if (imdbId != other.imdbId) return false
        if (revenue != other.revenue) return false
        if (budget != other.budget) return false
        if (runtime != other.runtime) return false
        if (popularity != other.popularity) return false
        if (tagline != other.tagline) return false
        if (homepage != other.homepage) return false
        if (voteCount != other.voteCount) return false
        if (genres != other.genres) return false
        if (isFavorite != other.isFavorite) return false
        if (isInWatchLater != other.isInWatchLater) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + imdbId.hashCode()
        result = 31 * result + revenue.hashCode()
        result = 31 * result + budget.hashCode()
        result = 31 * result + runtime
        result = 31 * result + popularity.hashCode()
        result = 31 * result + tagline.hashCode()
        result = 31 * result + homepage.hashCode()
        result = 31 * result + voteCount
        result = 31 * result + genres.hashCode()
        result = 31 * result + isFavorite.hashCode()
        result = 31 * result + isInWatchLater.hashCode()
        return result
    }

    override fun toString(): String {
        return "Movie(imdbId='$imdbId', revenue=$revenue, budget=$budget, runtime=$runtime, popularity=$popularity, tagline='$tagline', homepage='$homepage', voteCount=$voteCount, genres=$genres, isFavorite=$isFavorite, isInWatchList=$isInWatchLater) ${super.toString()}"
    }
}