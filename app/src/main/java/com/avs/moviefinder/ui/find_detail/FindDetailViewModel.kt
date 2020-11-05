package com.avs.moviefinder.ui.find_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avs.moviefinder.data.database.DatabaseManager
import com.avs.moviefinder.data.network.ErrorType
import com.avs.moviefinder.data.network.ServerApi
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.dto.MoviesSearchFilter
import com.avs.moviefinder.data.dto.Query
import com.avs.moviefinder.utils.BASE_URL
import com.avs.moviefinder.utils.RxBus
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*
import javax.inject.Inject

class FindDetailViewModel @Inject constructor(
    private val rxBus: RxBus,
    private val serverApi: ServerApi,
    private val databaseManager: DatabaseManager
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
    private var _query = MutableLiveData<String?>()
    private var rxBusDisposable: Disposable? = null
    private var apiDisposable: Disposable? = null
    private val dbDisposable = CompositeDisposable()

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
            is Movie -> {
                _movies.value?.let {
                    val fetchedMovie = _movies.value?.firstOrNull { it.id == event.id }
                    fetchedMovie?.let {
                        val updatedMovieIndex = _movies.value!!.indexOf(fetchedMovie)
                        if (updatedMovieIndex != -1) {
                            _movies.value!![updatedMovieIndex] = event
                            _updateMovieIndex.value = updatedMovieIndex
                            _updateMovieIndex.value = null
                        }
                    }
                }
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
            // todo zip the call with movies from the db
            apiDisposable = serverApi.getMovieByTitle(query)
        }
    }

    fun shareMovie(movieId: Long) {
        _shareBody.value = BASE_URL + "movie/" + movieId + "/"
        _shareBody.value = null
    }

    fun addToWatchLater(movieId: Long) {
        val movie = _movies.value?.firstOrNull { it.id == movieId }
        movie?.let {
            movie.isInWatchLater = !movie.isInWatchLater
            dbDisposable.add(databaseManager.insertMovie(movie))
            deleteMovieFromDB(movie)
        }
    }

    fun addToFavorites(movieId: Long) {
        val movie = _movies.value?.firstOrNull { it.id == movieId }
        movie?.let {
            movie.isFavorite = !movie.isFavorite
            dbDisposable.add(databaseManager.insertMovie(movie))
            deleteMovieFromDB(movie)
        }
    }

    private fun deleteMovieFromDB(movie: Movie) {
        if (!movie.isInWatchLater && !movie.isInWatchLater) {
            dbDisposable.add(databaseManager.delete(movie))
        }
    }

    override fun onCleared() {
        rxBusDisposable?.dispose()
        apiDisposable?.dispose()
        dbDisposable.clear()
        dbDisposable.dispose()
        super.onCleared()
    }
}