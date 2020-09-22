package com.avs.moviefinder.data.database

import androidx.room.TypeConverter
import com.avs.moviefinder.data.dto.Genre

open class GenresConverter {

    @TypeConverter
    open fun fromGenres(genres: List<Genre>): String {
        return genres.fold("") { names, genre ->
            if (names.isEmpty()) genre.name ?: "" else names + "," + genre.name
        }
    }

    @TypeConverter
    open fun toGenres(genres: String): List<Genre> {
        return genres.split(",").map { Genre(it.trim()) }
    }
}