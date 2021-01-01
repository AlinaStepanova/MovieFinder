package com.avs.moviefinder.data.database

import androidx.room.TypeConverter
import com.avs.moviefinder.data.dto.Country
import com.avs.moviefinder.data.dto.Genre

open class CountriesConverter {

    @TypeConverter
    open fun fromCountries(countries: List<Country>): String {
        return countries.fold("") { names, country ->
            if (names.isEmpty()) country.name ?: "" else names + "," + country.name
        }
    }

    @TypeConverter
    open fun toCountries(countries: String): List<Country> {
        return countries.split(",").map { Country(it.trim()) }
    }
}

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