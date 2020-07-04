package com.avs.moviefinder.ui.find

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avs.moviefinder.network.ErrorType
import com.avs.moviefinder.network.ServerApi
import com.avs.moviefinder.network.dto.Movie
import com.avs.moviefinder.network.dto.MoviesFilter
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
    private var _errorType = MutableLiveData<ErrorType?>()
    val errorType: LiveData<ErrorType?>
        get() = _errorType
    private var apiDisposable: Disposable? = null
    private var rxBusDisposable: Disposable? = null
    private var selectedSpinnerItem = 0

    init {
        rxBusDisposable = rxBus.events.subscribe { event -> handleServerResponse(event) }
    }

    private fun handleServerResponse(event: Any?) {
        if (event is MoviesFilter) {
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

    fun onSpinnerItemSelected(itemPosition: Int) {
        selectedSpinnerItem = itemPosition
        makeAPICall(itemPosition)
    }

    fun onRefresh() {
        makeAPICall(selectedSpinnerItem)
    }

    fun openMovieDetails(movieId: Long) {}

    fun shareMovie(movieId: Long) {}

    fun addToWatchLater(movieId: Long) {}

    fun addToWatched(movieId: Long) {}

    private fun makeAPICall(itemPosition: Int) {
        when (itemPosition) {
            0 -> {
                disposeValues()
                apiDisposable = serverApi.getPopularMovies()
            }
            1 -> {
                disposeValues()
                apiDisposable = serverApi.getTopRatedMovies()
            }
        }
    }

    private fun disposeValues() {
        _isProgressVisible.value = true
        _movies.value = ArrayList()
        apiDisposable?.dispose()
    }

    override fun onCleared() {
        apiDisposable?.dispose()
        rxBusDisposable?.dispose()
        super.onCleared()
    }
}