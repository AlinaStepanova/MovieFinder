package com.avs.moviefinder.data.network

import com.avs.moviefinder.data.dto.Credits
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.dto.Similar
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

    fun getMovieById(url: String): Single<Movie> {
        return moviesApi.getMovieById(url)
    }

    fun getVideos(movieId: Long): Single<Videos> {
        return moviesApi
            .getVideosByMovieId(movieId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getCredits(movieId: Long): Single<Credits> {
        return moviesApi
            .getCreditsByMovieId(movieId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getSimilar(movieId: Long): Single<Similar> {
        return moviesApi
            .getSimilarByMovieId(movieId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getPopularMoviesAsSingle(url: String) = moviesApi.getPopularMovies(url)

    fun getTopRatedMovies(url: String) = moviesApi.getTopRatedMovies(url)

    fun getNowPlayingMovies(url: String) = moviesApi.getNowPlayingMovies(url)

    fun getMovieByName(url: String) = moviesApi.getMovieByName(url)
}