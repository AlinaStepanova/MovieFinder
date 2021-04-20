package com.avs.moviefinder.data.dto

data class Result (
    var id: String? = null,
    var key: String? = null,
    var name: String? = null,
    var site: String? = null,
    var size: Int = 0,
    var type: String? = null,
    var backdrop_path: String? = null,
    var genre_ids: List<Int>? = null,
    var original_title: String? = null,
    var overview: String? = null,
    var poster_path: String? = null,
    var release_date: String? = null,
    var title: String? = null,
    var video: Boolean = false,
    var vote_average: Double = 0.0,
    var vote_count: Int = 0,
    var popularity: Double = 0.0,
)

data class Videos (var results: List<Result>? = null)