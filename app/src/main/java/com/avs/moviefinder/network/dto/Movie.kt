package com.avs.moviefinder.network.dto

data class Movie(
    val id: Int = 0,
    val title: String = "",
    val overview: String = "",
    val year: Int = 1920
)