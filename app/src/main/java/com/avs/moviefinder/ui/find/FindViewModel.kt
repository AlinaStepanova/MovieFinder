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
    private var _isProgressVisible = MutableLiveData<Boolean>()
    val isProgressVisible: LiveData<Boolean>
        get() = _isProgressVisible
    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading
    private var _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean>
        get() = _isError
    private var apiDisposable: Disposable? = null
    private var rxBusDisposable: Disposable? = null

    init {
        _isProgressVisible.value = true
        _isError.value = false
        apiDisposable = serverApi.getPopularMovies()
        rxBusDisposable = rxBus.events.subscribe { event -> handleServerResponse(event) }
    }

    private fun handleServerResponse(event: Any?) {
        if (event is MovieFilter) {
            _isProgressVisible.value = false
            _isLoading.value = false
            _isError.value = false
            _movies.value = event.movies
        } else if (event is Throwable) {
            _isProgressVisible.value = false
            _isLoading.value = false
            _isError.value = true
        }
    }

    fun onRefresh() {
        _movies.value = ArrayList()
        apiDisposable?.dispose()
        apiDisposable = serverApi.getPopularMovies()
    }

    override fun onCleared() {
        apiDisposable?.dispose()
        rxBusDisposable?.dispose()
        super.onCleared()
    }
}