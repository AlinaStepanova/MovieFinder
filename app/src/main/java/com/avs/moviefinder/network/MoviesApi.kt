package com.avs.moviefinder.network

import com.avs.moviefinder.network.dto.MovieFilter
import com.avs.moviefinder.utils.API_KEY
import io.reactivex.Single
import retrofit2.http.GET

/**
 * An interface used by Retrofit
 */
interface MoviesApi {

    @GET("popular?api_key=$API_KEY&language=en-US")
    fun getPopularMovies(): Single<MovieFilter>
}