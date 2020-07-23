package com.avs.moviefinder.network.dto

import com.google.gson.annotations.SerializedName
import java.util.*

data class MoviesFilter(@SerializedName("results") val movies: LinkedList<Movie> = LinkedList())

data class MoviesSearchFilter(@SerializedName("results") val movies: LinkedList<Movie> = LinkedList())