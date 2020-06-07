package com.avs.moviefinder.network

import com.avs.moviefinder.network.dto.MovieFilter
import com.avs.moviefinder.utils.API_KEY
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * An interface used by Retrofit
 */
interface MoviesApi {

    @GET("3/movie/popular?api_key=$API_KEY&language=en-US")
    fun getPopularMovies(): Single<MovieFilter>

    @GET("3/search/movie?api_key=$API_KEY&page=1&include_adult=false&language=en-US")
    fun getMovieByName(@Query("query") query: String): Single<MovieFilter>
}