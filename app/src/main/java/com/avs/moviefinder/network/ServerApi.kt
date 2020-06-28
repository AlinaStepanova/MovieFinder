package com.avs.moviefinder.network

import android.util.Log
import com.avs.moviefinder.BuildConfig
import com.avs.moviefinder.utils.RxBus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerApi @Inject constructor(
    private val rxBus: RxBus,
    private val moviesApi: MoviesApi
) {
    fun getPopularMovies(): Disposable {
        return moviesApi
            .getPopularMovies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ rxBus.send(it) }, { handleError(it) })
    }

    fun getTopRatedMovies(): Disposable {
        return moviesApi
            .getTopRatedMovies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ rxBus.send(it) }, { handleError(it) })
    }

    fun getMovieByTitle(title: String): Disposable {
        return moviesApi
            .getMovieByName(title)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ rxBus.send(it) }, { handleError(it) })
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