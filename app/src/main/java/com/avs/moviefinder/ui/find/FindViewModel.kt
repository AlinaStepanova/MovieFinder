package com.avs.moviefinder.ui.find

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avs.moviefinder.network.dto.Movie
import com.avs.moviefinder.network.ServerApi
import com.avs.moviefinder.network.dto.MovieFilter
import com.avs.moviefinder.utils.RxBus
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class FindViewModel @Inject constructor(
    private val serverApi: ServerApi,
    rxBus: RxBus
) : ViewModel() {

    private var _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>>
        get() = _movies
    private var apiDisposable: Disposable? = null
    private var rxBusDisposable: Disposable? = null

    init {
        apiDisposable = serverApi.getPopularMovies()
        rxBusDisposable = rxBus.events.subscribe { event -> handleServerResponse(event) }
    }

    private fun handleServerResponse(event: Any?) {
        if (event is MovieFilter) {
            _movies.value = event.movies
        }
        Log.d("jjj", event.toString())
    }

    override fun onCleared() {
        apiDisposable?.dispose()
        rxBusDisposable?.dispose()
        super.onCleared()
    }
}