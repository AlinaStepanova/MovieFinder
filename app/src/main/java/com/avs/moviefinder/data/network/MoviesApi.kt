package com.avs.moviefinder.data.network

import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.dto.MoviesAPIFilter
import com.avs.moviefinder.data.dto.MoviesSearchFilter
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

    @GET("3/movie/popular?api_key=$API_KEY&language=en-US&include_adult=false")
    fun getPopularMovies(): Single<MoviesAPIFilter>

    @GET("3/movie/top_rated?api_key=$API_KEY&language=en-US&include_adult=false")
    fun getTopRatedMovies(): Single<MoviesAPIFilter>

    @GET
    fun getNowPlayingMovies(@Url url: String): Single<MoviesAPIFilter>

    @GET("3/search/movie?api_key=$API_KEY&page=1&include_adult=false&language=en-US")
    fun getMovieByName(@Query("query") query: String): Single<MoviesSearchFilter>

    @GET("3/movie/{id}?api_key=$API_KEY")
    fun getMovieById(@Path("id") id: Long): Single<Movie>
}