package com.avs.moviefinder.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avs.moviefinder.network.ErrorType
import com.avs.moviefinder.network.ServerApi
import com.avs.moviefinder.network.dto.Movie
import com.avs.moviefinder.network.dto.MoviesFilter
import com.avs.moviefinder.utils.BASE_URL
import com.avs.moviefinder.utils.RxBus
import io.reactivex.disposables.Disposable
import java.util.*
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val serverApi: ServerApi,
    rxBus: RxBus
) : ViewModel() {

    private var _movies = MutableLiveData<LinkedList<Movie>>()
    val movies: LiveData<LinkedList<Movie>>
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
    private var _shareBody = MutableLiveData<String?>()
    val shareBody: LiveData<String?>
        get() = _shareBody
    private var apiDisposable: Disposable? = null
    private var rxBusDisposable: Disposable? = null
    private var _selectedCategory = MutableLiveData<MoviesCategory>()
    val selectedCategory: LiveData<MoviesCategory>
        get() = _selectedCategory

    init {
        if (_selectedCategory.value == null) {
            _selectedCategory.value = MoviesCategory.POPULAR
            getPopularMovies()
        }
        rxBusDisposable = rxBus.events.subscribe { event -> handleServerResponse(event) }
    }

    private fun handleServerResponse(event: Any?) {
        if (event is MoviesFilter) {
            _isProgressVisible.value = false
            _isLoading.value = false
            if (event.movies.isEmpty()) _errorType.value =
                ErrorType.NO_RESULTS else _errorType.value = null
            val movies = event.movies
            if (movies.first.id != 0L) {
                movies.addFirst(Movie())
            }
            _movies.value = movies
        } else if (event is Throwable) {
            _isProgressVisible.value = false
            _isLoading.value = false
            _errorType.value = ErrorType.NETWORK
        }
    }

    fun onRefresh() {
        makeAPICall()
    }

    fun openMovieDetails(movieId: Long) {
        apiDisposable?.dispose()
        apiDisposable = serverApi.getMovieById(movieId)
    }

    fun shareMovie(movieId: Long) {
        _shareBody.value = BASE_URL + "movie/" + movieId + "/"
        _shareBody.value = null
    }

    fun addToWatchLater(movieId: Long) {}

    fun addToWatched(movieId: Long) {}

    private fun makeAPICall() {
        when (_selectedCategory.value) {
            MoviesCategory.POPULAR -> {
                getPopularMovies()
            }
            MoviesCategory.TOP_RATED -> {
                getTopRatedMovies()
            }
        }
    }

    fun onPopularClick() {
        if (_selectedCategory.value == MoviesCategory.TOP_RATED) {
            _selectedCategory.value = MoviesCategory.POPULAR
            getPopularMovies()
        }
    }

    fun onTopRatedClick() {
        if (_selectedCategory.value == MoviesCategory.POPULAR) {
            _selectedCategory.value = MoviesCategory.TOP_RATED
            getTopRatedMovies()
        }
    }

    private fun getPopularMovies() {
        disposeValues()
        apiDisposable = serverApi.getPopularMovies()
    }

    private fun getTopRatedMovies() {
        disposeValues()
        apiDisposable = serverApi.getTopRatedMovies()
    }

    private fun disposeValues() {
        _isProgressVisible.value = true
        _movies.value = LinkedList()
        apiDisposable?.dispose()
    }

    override fun onCleared() {
        apiDisposable?.dispose()
        rxBusDisposable?.dispose()
        super.onCleared()
    }
}