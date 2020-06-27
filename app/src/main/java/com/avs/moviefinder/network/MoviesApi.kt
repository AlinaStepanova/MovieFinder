package com.avs.moviefinder.network

import com.avs.moviefinder.network.dto.MoviesFilter
import com.avs.moviefinder.network.dto.MoviesSearchFilter
import com.avs.moviefinder.utils.API_KEY
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * An interface used by Retrofit
 */
interface MoviesApi {

    @GET("3/movie/popular?api_key=$API_KEY&language=en-US&include_adult=false")
    fun getPopularMovies(): Single<MoviesFilter>

    @GET("3/movie/top_rated?api_key=$API_KEY&language=en-US&include_adult=false")
    fun getTopRatedMovies(): Single<MoviesFilter>

    @GET("3/movie/now_playing?api_key=$API_KEY&language=en-US&include_adult=false")
    fun getNowPlayingMovies(): Single<MoviesFilter>

    @GET("3/search/movie?api_key=$API_KEY&page=1&include_adult=false&language=en-US")
    fun getMovieByName(@Query("query") query: String): Single<MoviesSearchFilter>
}