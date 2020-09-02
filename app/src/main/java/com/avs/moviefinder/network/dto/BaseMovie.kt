package com.avs.moviefinder.network.dto

import com.google.gson.annotations.SerializedName

open class BaseMovie(
    val id: Long = 0,
    val title: String = "",
    val overview: String = "",
    @SerializedName("poster_path")
    val posterPath: String = "",
    @SerializedName("release_date")
    val releaseDate: String = "",
    @SerializedName("vote_average")
    val rating: String = ""
) {
    override fun toString(): String {
        return "BaseMovie(id=$id, title='$title', overview='$overview', posterPath='$posterPath', year='$releaseDate', rating='$rating')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseMovie) return false

        if (id != other.id) return false
        if (title != other.title) return false
        if (overview != other.overview) return false
        if (posterPath != other.posterPath) return false
        if (releaseDate != other.releaseDate) return false
        if (rating != other.rating) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + overview.hashCode()
        result = 31 * result + posterPath.hashCode()
        result = 31 * result + releaseDate.hashCode()
        result = 31 * result + rating.hashCode()
        return result
    }
}