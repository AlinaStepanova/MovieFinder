package com.avs.moviefinder.data.network

import android.util.Log
import com.avs.moviefinder.BuildConfig
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.dto.MoviesAPIFilter
import com.avs.moviefinder.data.dto.MoviesSearchFilter
import com.avs.moviefinder.utils.RxBus
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerApi @Inject constructor(
    private val rxBus: RxBus,
    private val moviesApi: MoviesApi
) {

    fun getPopularMoviesAsSingle(url: String): Single<MoviesAPIFilter> {
        return moviesApi
            .getPopularMovies(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getMovieById(id: Long): Single<Movie> {
        return moviesApi.getMovieById(id)
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

    fun getMovieByTitle(title: String): Single<MoviesSearchFilter> {
        return moviesApi
            .getMovieByName(title)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun handleError(error: Throwable?) {
        if (BuildConfig.DEBUG) {
            if (error != null) {
                rxBus.send(error)
            }
            Log.d(this.javaClass.simpleName, error.toString())
        }
    }
}