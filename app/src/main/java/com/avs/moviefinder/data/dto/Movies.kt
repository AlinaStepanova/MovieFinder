package com.avs.moviefinder.data.dto

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

data class MoviesAPIFilter(@SerializedName("results") val movies: LinkedList<Movie> = LinkedList())

data class MoviesSearchFilter(@SerializedName("results") val movies: LinkedList<Movie> = LinkedList())

data class Videos(@SerializedName("results") val videos: ArrayList<Result> = ArrayList())

data class Similar(@SerializedName("results") val similar: ArrayList<Result> = ArrayList())

data class MoviesFilterResult(val movies: LinkedList<Movie> = LinkedList())

data class FavoritesList(val movies: List<Movie>? = LinkedList())

data class WatchList(val movies: List<Movie>? = LinkedList())