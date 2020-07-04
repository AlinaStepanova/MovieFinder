package com.avs.moviefinder.ui.find_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avs.moviefinder.network.ErrorType
import com.avs.moviefinder.network.dto.Movie
import com.avs.moviefinder.network.dto.MoviesSearchFilter
import com.avs.moviefinder.utils.RxBus
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class FindDetailViewModel @Inject constructor(
    rxBus: RxBus
) : ViewModel() {

    private var _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>>
        get() = _movies
    private var _isProgressVisible = MutableLiveData<Boolean>()
    val isProgressVisible: LiveData<Boolean>
        get() = _isProgressVisible
    private var _isLoading = MutableLiveData<Boolean>()
    private var _errorType = MutableLiveData<ErrorType?>()
    val errorType: LiveData<ErrorType?>
        get() = _errorType
    private var rxBusDisposable: Disposable? = null

    init {
        _isProgressVisible.value = true
        _errorType.value = null
        rxBusDisposable = rxBus.events.subscribe { event -> handleServerResponse(event) }
    }

    private fun handleServerResponse(event: Any?) {
        if (event is MoviesSearchFilter) {
            _isProgressVisible.value = false
            _isLoading.value = false
            if (event.movies.isEmpty()) _errorType.value =
                ErrorType.NO_RESULTS else _errorType.value = null
            _movies.value = event.movies
        } else if (event is Throwable) {
            _isProgressVisible.value = false
            _isLoading.value = false
            _errorType.value = ErrorType.NETWORK
        }
    }

    fun openMovieDetails(movieId: Long) {}

    fun shareMovie(movieId: Long) {}

    fun addToWatchLater(movieId: Long) {}

    fun addToWatched(movieId: Long) {}

    override fun onCleared() {
        rxBusDisposable?.dispose()
        super.onCleared()
    }
}