package com.avs.moviefinder.network.dto

import com.google.gson.annotations.SerializedName
import java.util.*

data class MoviesFilter(@SerializedName("results") val baseMovies: LinkedList<BaseMovie> = LinkedList())

data class MoviesSearchFilter(@SerializedName("results") val baseMovies: LinkedList<BaseMovie> = LinkedList())