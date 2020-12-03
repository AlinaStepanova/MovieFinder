package com.avs.moviefinder.ui.find_detail

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avs.moviefinder.data.database.DatabaseManager
import com.avs.moviefinder.data.dto.*
import com.avs.moviefinder.data.network.ErrorType
import com.avs.moviefinder.data.network.ServerApi
import com.avs.moviefinder.ui.MOVIE_EXTRA_TAG
import com.avs.moviefinder.utils.BASE_URL
import com.avs.moviefinder.utils.IS_MOVIE_UPDATED_EXTRA
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
    private var _moviesDB = MutableLiveData<ArrayList<Movie>>()
    private var _query = MutableLiveData<String?>()
    private var rxBusDisposable: Disposable? = null
    private var apiDisposable: Disposable? = null
    private val dbDisposable = CompositeDisposable()

    init {
        _isProgressVisible.value = true
        _errorType.value = null
        rxBusDisposable = rxBus.events.subscribe { event -> handleServerResponse(event) }
    }

    override fun onCleared() {
        rxBusDisposable?.dispose()
        apiDisposable?.dispose()
        dbDisposable.clear()
        dbDisposable.dispose()
        super.onCleared()
    }

    private fun handleServerResponse(event: Any?) {
        when (event) {
            is MoviesSearchFilter -> {
                _isProgressVisible.value = false
                _isLoading.value = false
                if (event.movies.isEmpty()) _errorType.value =
                    ErrorType.NO_RESULTS else _errorType.value = null
                val movies = event.movies
                combineServerAndDatabaseData(movies)
                _movies.value = movies
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
            is MoviesDBFilter -> {
                _moviesDB.value = event.movies as ArrayList<Movie>
                getQueryByTitle(_query.value)
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

    private fun combineServerAndDatabaseData(movies: LinkedList<Movie>) {
        movies.forEach { movie ->
            val insertedMovie = _moviesDB.value!!.firstOrNull { it.id == movie.id }
            if (insertedMovie != null) {
                movie.isInWatchLater = insertedMovie.isInWatchLater
                movie.isFavorite = insertedMovie.isFavorite
            }
        }
    }

    private fun getQueryByTitle(query: String?) {
        apiDisposable?.dispose()
        if (query != null) {
            apiDisposable = serverApi.getMovieByTitle(query)
        }
    }

    private fun deleteMovieFromDB(movie: Movie) {
        if (!movie.isInWatchLater && !movie.isFavorite) {
            dbDisposable.add(databaseManager.delete(movie))
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
            it.lastTimeUpdated = System.currentTimeMillis()
            dbDisposable.add(databaseManager.insertMovie(movie))
            deleteMovieFromDB(movie)
        }
    }

    fun onQuerySubmitted(query: String?) {
        if (query != null) {
            dbDisposable.add(databaseManager.getAllMovies())
            _query.value = query
        }
    }

    fun addToFavorites(movieId: Long) {
        val movie = _movies.value?.firstOrNull { it.id == movieId }
        movie?.let {
            movie.isFavorite = !movie.isFavorite
            it.lastTimeUpdated = System.currentTimeMillis()
            dbDisposable.add(databaseManager.insertMovie(movie))
            deleteMovieFromDB(movie)
        }
    }

    fun handleOnActivityResult(resultIntent: Intent) {
        val isMovieUpdated = resultIntent.getBooleanExtra(IS_MOVIE_UPDATED_EXTRA, false)
        if (isMovieUpdated) {
            val updatedMovie = resultIntent.getParcelableExtra<Movie>(MOVIE_EXTRA_TAG)
            if (updatedMovie != null && updatedMovie.id > 0) {
                // todo think if the existing logic can be reused
                dbDisposable.add(databaseManager.update(updatedMovie))
            }
        }
    }
}