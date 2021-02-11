package com.avs.moviefinder.ui.find_detail

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.dto.MoviesSearchFilter
import com.avs.moviefinder.data.dto.Query
import com.avs.moviefinder.data.network.ErrorType
import com.avs.moviefinder.repository.FindDetailsRepository
import com.avs.moviefinder.ui.MOVIE_EXTRA_TAG
import com.avs.moviefinder.utils.IS_MOVIE_UPDATED_EXTRA
import com.avs.moviefinder.utils.RxBus
import com.avs.moviefinder.utils.buildShareLink
import io.reactivex.disposables.Disposable
import java.util.*
import javax.inject.Inject

class FindDetailViewModel @Inject constructor(
    private val rxBus: RxBus,
    private val findDetailsRepository: FindDetailsRepository
) : ViewModel() {

    private var _movies = MutableLiveData<LinkedList<Movie>>()
    val movies: LiveData<LinkedList<Movie>>
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
    private var _updateMovieIndex = MutableLiveData<Int?>()
    val updateMovieIndex: LiveData<Int?>
        get() = _updateMovieIndex
    private var _isBackOnline = MutableLiveData<Boolean?>()
    val isBackOnline: LiveData<Boolean?>
        get() = _isBackOnline
    private var _query = MutableLiveData<String?>()
    private var _initialQuery = MutableLiveData<String?>()
    private var rxBusDisposable: Disposable? = null

    init {
        _isProgressVisible.value = true
        _errorType.value = null
    }

    override fun onCleared() {
        unsubscribeFromEvents()
        findDetailsRepository.clear()
        super.onCleared()
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
            is Movie -> {
                _movies.value?.let { list ->
                    val fetchedMovie = list.firstOrNull { it.id == event.id }
                    fetchedMovie?.let {
                        val updatedMovieIndex = list.indexOf(it)
                        if (updatedMovieIndex != -1) {
                            list[updatedMovieIndex] = event
                            _updateMovieIndex.value = updatedMovieIndex
                            _updateMovieIndex.value = null
                        }
                    }
                }
            }
            is Query -> {
                onQuerySubmitted(event.query)
            }
            is Throwable -> {
                _isProgressVisible.value = false
                _isLoading.value = false
                _movies.value = LinkedList()
                _errorType.value = ErrorType.NETWORK
            }
        }
    }

    private fun onQuerySubmitted(query: String?) {
        if (query != null) {
            findDetailsRepository.getSubmittedQuery(query)
            _query.value = query
        }
    }

    private fun getQueryByTitle(query: String?) {
        if (query != null) {
            findDetailsRepository.getSubmittedQuery(query)
        }
    }

    private fun deleteMovieFromDB(movie: Movie) {
        if (!movie.isInWatchLater && !movie.isFavorite) {
            findDetailsRepository.deleteMovie(movie)
        }
    }

    fun searchInitialQuery(query: String?) {
        if (_initialQuery.value == null) {
            _initialQuery.value = query
            onQuerySubmitted(query)
        }
    }

    fun subscribeToEvents() {
        unsubscribeFromEvents()
        rxBusDisposable = rxBus.events.subscribe { event -> handleServerResponse(event) }
    }

    fun unsubscribeFromEvents() {
        rxBusDisposable?.dispose()
    }

    fun reactOnNetworkChangeState(isActive: Boolean) {
        if (isActive && _errorType.value == ErrorType.NETWORK) {
            getQueryByTitle(_query.value)
            _isBackOnline.value = true
            _isBackOnline.value = null
        }
    }

    fun shareMovie(movieId: Long) {
        _shareBody.value = buildShareLink(movieId)
        _shareBody.value = null
    }

    fun addToWatchLater(movieId: Long) {
        val movie = _movies.value?.firstOrNull { it.id == movieId }
        movie?.let {
            movie.isInWatchLater = !movie.isInWatchLater
            it.lastTimeUpdated = System.currentTimeMillis()
            findDetailsRepository.insertMovie(movie)
            deleteMovieFromDB(movie)
        }
    }

    fun addToFavorites(movieId: Long) {
        val movie = _movies.value?.firstOrNull { it.id == movieId }
        movie?.let {
            movie.isFavorite = !movie.isFavorite
            it.lastTimeUpdated = System.currentTimeMillis()
            findDetailsRepository.insertMovie(movie)
            deleteMovieFromDB(movie)
        }
    }

    fun handleOnActivityResult(resultIntent: Intent) {
        val isMovieUpdated = resultIntent.getBooleanExtra(IS_MOVIE_UPDATED_EXTRA, false)
        if (isMovieUpdated) {
            val updatedMovie = resultIntent.getParcelableExtra<Movie>(MOVIE_EXTRA_TAG)
            if (updatedMovie != null && updatedMovie.id > 0) {
                findDetailsRepository.updateMovie(updatedMovie)
            }
        }
    }
}