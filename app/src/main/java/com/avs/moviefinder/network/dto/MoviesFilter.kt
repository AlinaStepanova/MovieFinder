package com.avs.moviefinder.network.dto

import com.google.gson.annotations.SerializedName

data class MoviesFilter(@SerializedName("results") val movies: List<Movie> = ArrayList())

data class MoviesSearchFilter(@SerializedName("results") val movies: List<Movie> = ArrayList())