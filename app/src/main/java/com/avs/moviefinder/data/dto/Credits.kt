package com.avs.moviefinder.data.dto

import com.google.gson.annotations.SerializedName

data class Credits(var cast: List<Cast> = ArrayList(), var crew: List<Crew> = ArrayList())

data class Cast(
    var id: Int = 0,
    var name: String? = "",
    var popularity: Double = 0.0,
    @SerializedName("profile_path")
    var profilePath: String? = "",
    @SerializedName("cast_id")
    var castId: Int = 0,
    var character: String? = "",
    @SerializedName("credit_id")
    var creditId: String? = "",
)

data class Crew(
    var id: Int = 0,
    var name: String? = "",
    var popularity: Double = 0.0,
    @SerializedName("profile_path")
    var profilePath: String? = "",
    @SerializedName("cast_id")
    var castId: Int = 0,
    var department: String? = "",
    var job: String? = ""
)