package com.avs.moviefinder.data.network

import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.dto.MoviesAPIFilter
import com.avs.moviefinder.data.dto.MoviesSearchFilter
import com.avs.moviefinder.data.dto.Videos
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerApi @Inject constructor(
    private val moviesApi: MoviesApi
) {

    fun getPopularMoviesAsSingle(url: String): Single<MoviesAPIFilter> {
        return moviesApi
            .getPopularMovies(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getMovieById(url: String): Single<Movie> {
        return moviesApi.getMovieById(url)
    }

    fun getVideos(movieId: Long): Single<Videos> {
        return moviesApi
            .getVideosByMovieId(movieId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getTopRatedMovies(url: String): Single<MoviesAPIFilter> {
        return moviesApi
            .getTopRatedMovies(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getNowPlayingMovies(url: String): Single<MoviesAPIFilter> {
        return moviesApi
            .getNowPlayingMovies(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getMovieByTitle(url: String): Single<MoviesSearchFilter> {
        return moviesApi
            .getMovieByName(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}