package com.avs.moviefinder.network

import android.util.Log
import com.avs.moviefinder.BuildConfig
import com.avs.moviefinder.utils.RxBus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerApi @Inject constructor(
    private val rxBus: RxBus,
    private val ratesApi: MoviesApi
) {
    fun getPopularMovies(): Disposable {
        return ratesApi
            .getPopularMovies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ checkResponse(it) }, { handleError(it) })
    }

    private fun checkResponse(response: Response<Any>) {
        if (response.body() != null && response.isSuccessful) {
            Log.d(this.javaClass.simpleName, response.body().toString())
        }
    }

    private fun handleError(error: Throwable?) {
        if (BuildConfig.DEBUG) {
            Log.d(this.javaClass.simpleName, error.toString())
        }
    }
}