package com.avs.moviefinder.data.network

import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.dto.MoviesAPIFilter
import com.avs.moviefinder.data.dto.MoviesSearchFilter
import io.reactivex.Single
import retrofit2.http.GET
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

    @GET
    fun getMovieById(@Url url: String): Single<Movie>
}