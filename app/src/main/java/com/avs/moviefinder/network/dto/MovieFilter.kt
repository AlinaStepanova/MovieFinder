package com.avs.moviefinder.network.dto

import com.google.gson.annotations.SerializedName

data class MovieFilter(@SerializedName("results") val movies: List<Movie> = ArrayList())