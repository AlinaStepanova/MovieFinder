package com.avs.moviefinder.data.network

import com.avs.moviefinder.data.dto.*
import com.avs.moviefinder.utils.API_KEY
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * An interface used by Retrofit
 */
interface MoviesApi {

    @GET
    fun getPopularMovies(@Url url: String): Single<MoviesAPIFilter>

    @GET
    fun getTopRatedMovies(@Url url: String): Single<MoviesAPIFilter>

    @GET
    fun getNowPlayingMovies(@Url url: String): Single<MoviesAPIFilter>

    @GET
    fun getMovieByName(@Url url: String): Single<MoviesSearchFilter>

    @GET("3/search/movie?api_key=$API_KEY")
    fun movieByName(
        @Query("page") page: Int,
        @Query("language") language: String,
        @Query("query") query: String
    ) : Single<MoviesResponse>

    @GET
    fun getMovieById(@Url url: String): Single<Movie>

    @GET("3/movie/{movie_id}/videos?api_key=$API_KEY&language=en-US&include_adult=false")
    fun getVideosByMovieId(@Path("movie_id") movieId: Long): Single<Videos>

    @GET("3/movie/{movie_id}/credits?api_key=$API_KEY&language=en-US&include_adult=false")
    fun getCreditsByMovieId(@Path("movie_id") movieId: Long): Single<Credits>

    @GET("3/movie/{movie_id}/similar?api_key=$API_KEY&language=en-US&include_adult=false")
    fun getSimilarByMovieId(@Path("movie_id") movieId: Long): Single<Similar>
}