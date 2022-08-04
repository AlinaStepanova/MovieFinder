package com.avs.moviefinder.data.network

import com.avs.moviefinder.data.dto.*
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

/**
 * An interface used by Retrofit
 */
interface MoviesApi {

    @GET
    fun getMovies(@Url url: String): Single<MoviesResponse>

    @GET
    fun getMovieByName(@Url url: String): Single<MoviesResponse>

    @GET
    fun getMovieById(@Url url: String): Single<Movie>

    @GET("3/movie/{movie_id}/videos?language=en-US&include_adult=false")
    fun getVideosByMovieId(@Path("movie_id") movieId: Long): Single<Videos>

    @GET("3/movie/{movie_id}/credits?language=en-US&include_adult=false")
    fun getCreditsByMovieId(@Path("movie_id") movieId: Long): Single<Credits>

    @GET("3/movie/{movie_id}/similar?language=en-US&include_adult=false")
    fun getSimilarByMovieId(@Path("movie_id") movieId: Long): Single<Similar>
}