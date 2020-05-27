package com.avs.moviefinder.network

import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET

/**
 * An interface used by Retrofit
 */
interface MoviesApi {

    @GET("popular?api_key=3ae41cba5ffccb37830367a69287433d&language=en-US")
        fun getPopularMovies(): Single<Response<Any>>
}