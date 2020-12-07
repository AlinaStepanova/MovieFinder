package com.avs.moviefinder.data.database

import androidx.room.TypeConverter
import com.avs.moviefinder.data.dto.Country

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