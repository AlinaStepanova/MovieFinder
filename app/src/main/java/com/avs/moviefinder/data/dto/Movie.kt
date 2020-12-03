package com.avs.moviefinder.data.dto

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "movie_table")
data class Movie(
    @PrimaryKey
    var id: Long = 0,
    var title: String? = "",
    var overview: String? = "",
    @SerializedName("poster_path")
    @ColumnInfo(name = "poster_path")
    var posterPath: String? = "",
    @SerializedName("release_date")
    @ColumnInfo(name = "release_date")
    var releaseDate: String? = "",
    @SerializedName("vote_average")
    var rating: String? = "",
    @SerializedName("imdb_id")
    @ColumnInfo(name = "imdb_id")
    var imdbId: String? = "",
    var revenue: Double = 0.0,
    var budget: Double = 0.0,
    var runtime: Int = 0,
    var popularity: Double = 0.0,
    var tagline: String? = "",
    var homepage: String? = "",
    @SerializedName("vote_count")
    @ColumnInfo(name = "vote_count")
    var voteCount: Int = 0,
    @SerializedName("genres")
    var genres: List<Genre>? = ArrayList(),
    var isFavorite: Boolean = false,
    var isInWatchLater: Boolean = false,
    var lastTimeUpdated: Long = System.currentTimeMillis(),
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.createTypedArrayList(Genre),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(title)
        parcel.writeString(overview)
        parcel.writeString(posterPath)
        parcel.writeString(releaseDate)
        parcel.writeString(rating)
        parcel.writeString(imdbId)
        parcel.writeDouble(revenue)
        parcel.writeDouble(budget)
        parcel.writeInt(runtime)
        parcel.writeDouble(popularity)
        parcel.writeString(tagline)
        parcel.writeString(homepage)
        parcel.writeInt(voteCount)
        parcel.writeTypedList(genres)
        parcel.writeByte(if (isFavorite) 1 else 0)
        parcel.writeByte(if (isInWatchLater) 1 else 0)
        parcel.writeLong(lastTimeUpdated)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Movie> {
        override fun createFromParcel(parcel: Parcel): Movie {
            return Movie(parcel)
        }

        override fun newArray(size: Int): Array<Movie?> {
            return arrayOfNulls(size)
        }
    }
}