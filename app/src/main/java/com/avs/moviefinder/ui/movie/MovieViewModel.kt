package com.avs.moviefinder.ui.movie

import android.util.Log
import androidx.lifecycle.ViewModel
import com.avs.moviefinder.network.ServerApi
import com.avs.moviefinder.network.dto.BaseMovie
import com.avs.moviefinder.network.dto.Movie
import com.avs.moviefinder.utils.RxBus
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class MovieViewModel @Inject constructor(
    private val serverApi: ServerApi,
    private val rxBus: RxBus
) : ViewModel() {
    private var rxBusDisposable: Disposable? = null
    private var apiDisposable: Disposable? = null

    init {
        rxBusDisposable = rxBus.events.subscribe { event -> handleServerResponse(event) }
    }

    private fun handleServerResponse(event: Any?) {
        if (event is Movie) {
            Log.d("jjjk", event.toString())
        } else if (event is Throwable) {
           // todo add error handling
        }
    }

    fun openMovieDetails(movieId: Long?) {
        if (movieId != null) {
            apiDisposable?.dispose()
            apiDisposable = serverApi.getMovieById(movieId)
        }
    }

    override fun onCleared() {
        apiDisposable?.dispose()
        rxBusDisposable?.dispose()
        super.onCleared()
    }


}