package com.avs.moviefinder.data.dto

import com.google.gson.annotations.SerializedName
import java.util.*

data class MoviesFilter(@SerializedName("results") val Movies: LinkedList<Movie> = LinkedList())

data class MoviesSearchFilter(@SerializedName("results") val Movies: LinkedList<Movie> = LinkedList())