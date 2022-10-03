package com.avs.moviefinder.data.dto

import androidx.paging.PagingData
import com.google.gson.annotations.SerializedName
import java.util.*

data class Videos(@SerializedName("results") val videos: ArrayList<Result> = ArrayList())

data class Similar(@SerializedName("results") val similar: ArrayList<Result> = ArrayList())

data class FavoritesList(val movies: PagingData<Movie>)

data class WatchList(val movies: PagingData<Movie>)

data class PagingDataList(val movies: PagingData<Movie>)

data class PagingSearchDataList(val movies: PagingData<Movie>)

data class MoviesResponse(
    @SerializedName("total_pages") val total: Int = 0,
    val page: Int = 0,
    val results: List<Movie>
)