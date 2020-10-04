package com.avs.moviefinder.data.dto

import com.google.gson.annotations.SerializedName
import java.util.*

data class MoviesAPIFilter(@SerializedName("results") val movies: LinkedList<Movie> = LinkedList())

data class MoviesSearchFilter(@SerializedName("results") val movies: LinkedList<Movie> = LinkedList())

data class MoviesDBFilter(val movies: List<Movie> = LinkedList())

data class FavoritesList(val movies: List<Movie>? = LinkedList())

data class WatchList(val movies: List<Movie>? = LinkedList())