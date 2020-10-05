package com.avs.moviefinder.ui.find_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avs.moviefinder.data.network.ErrorType
import com.avs.moviefinder.data.network.ServerApi
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.dto.MoviesSearchFilter
import com.avs.moviefinder.data.dto.Query
import com.avs.moviefinder.utils.BASE_URL
import com.avs.moviefinder.utils.RxBus
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class FindDetailViewModel @Inject constructor(
    private val rxBus: RxBus,
    private val serverApi: ServerApi
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
    private var _shareBody = MutableLiveData<String?>()
    val shareBody: LiveData<String?>
        get() = _shareBody
    private var _query = MutableLiveData<String?>()
    private var rxBusDisposable: Disposable? = null
    private var apiDisposable: Disposable? = null

    init {
        _isProgressVisible.value = true
        _errorType.value = null
        rxBusDisposable = rxBus.events.subscribe { event -> handleServerResponse(event) }
    }

    private fun handleServerResponse(event: Any?) {
        when (event) {
            is MoviesSearchFilter -> {
                _isProgressVisible.value = false
                _isLoading.value = false
                if (event.movies.isEmpty()) _errorType.value =
                    ErrorType.NO_RESULTS else _errorType.value = null
                _movies.value = event.movies
            }
            is Throwable -> {
                _isProgressVisible.value = false
                _isLoading.value = false
                _errorType.value = ErrorType.NETWORK
            }
            is Query -> {
                onQuerySubmitted(event.query)
            }
        }
    }

    fun onQueryFirstSubmitted(query: String?) {
        if (_query.value == null) {
            onQuerySubmitted(query)
        }
    }

    private fun onQuerySubmitted(query: String?) {
        apiDisposable?.dispose()
        if (query != null) {
            _query.value = query
            apiDisposable = serverApi.getMovieByTitle(query)
        }
    }

    fun shareMovie(movieId: Long) {
        _shareBody.value = BASE_URL + "movie/" + movieId + "/"
        _shareBody.value = null
    }

    fun addToWatchLater(movieId: Long) {}

    fun addToFavorites(movieId: Long) {}

    override fun onCleared() {
        rxBusDisposable?.dispose()
        apiDisposable?.dispose()
        super.onCleared()
    }
}