package com.avs.moviefinder.data.dto

data class Cast(
    var id: Int = 0,
    var known_for_department: String? = null,
    var name: String? = null,
    var popularity: Double = 0.0,
    var profile_path: String? = null,
    var cast_id: Int = 0,
    var character: String? = null,
    var credit_id: String? = null,
)

data class Crew(
    var id: Int = 0,
    var name: String? = null,
    var popularity: Double = 0.0,
    var profile_path: String? = null,
    var credit_id: String? = null,
    var department: String? = null,
    var job: String? = null
)

data class Credits(
    var cast: List<Cast>? = null,
    var crew: List<Crew>? = null
)