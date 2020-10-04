package com.avs.moviefinder.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avs.moviefinder.data.database.DatabaseManager
import com.avs.moviefinder.data.network.ErrorType
import com.avs.moviefinder.data.network.ServerApi
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.dto.MoviesAPIFilter
import com.avs.moviefinder.data.dto.MoviesDBFilter
import com.avs.moviefinder.utils.BASE_URL
import com.avs.moviefinder.utils.RxBus
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val serverApi: ServerApi,
    rxBus: RxBus,
    private val databaseManager: DatabaseManager
) : ViewModel() {

    private var _movies = MutableLiveData<LinkedList<Movie>>()
    val movies: LiveData<LinkedList<Movie>>
        get() = _movies
    private var _moviesDB = MutableLiveData<ArrayList<Movie>>()
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
    private val dbDisposable = CompositeDisposable()
    private var _selectedCategory = MutableLiveData<MoviesCategory>()
    val selectedCategory: LiveData<MoviesCategory>
        get() = _selectedCategory
    private var _updateMovieIndex = MutableLiveData<Int?>()
    val updateMovieIndex: LiveData<Int?>
        get() = _updateMovieIndex

    init {
        rxBusDisposable = rxBus.events.subscribe { event -> handleServerResponse(event) }
        dbDisposable.add(databaseManager.getAllMovies())
    }

    private fun handleServerResponse(event: Any?) {
        when (event) {
            is MoviesDBFilter -> {
                _moviesDB.value = event.movies as ArrayList<Movie>
                makeAPICall()
            }
            is MoviesAPIFilter -> {
                _isProgressVisible.value = false
                _isLoading.value = false
                if (event.movies.isEmpty()) _errorType.value =
                    ErrorType.NO_RESULTS else _errorType.value = null
                val movies = event.movies
                combineServerAndDatabaseData(event, movies)
                if (movies.first.id != 0L) {
                    movies.addFirst(Movie())
                }
                _movies.value = movies
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
        }
    }

    private fun combineServerAndDatabaseData(
        event: MoviesAPIFilter,
        movies: LinkedList<Movie>
    ) {
        if (_moviesDB.value!!.isEmpty()) {
            dbDisposable.add(databaseManager.insertMovies(event.movies))
        } else {
            movies.forEach { movie ->
                val insertedMovie = _moviesDB.value!!.firstOrNull { it.id == movie.id }
                if (insertedMovie != null) {
                    movie.isInWatchLater = insertedMovie.isInWatchLater
                    movie.isFavorite = insertedMovie.isFavorite
                } else if (movie.id != 0L) {
                    dbDisposable.add(databaseManager.insertMovie(movie))
                }
            }
        }
    }

    fun onRefresh() {
        dbDisposable.add(databaseManager.getAllMovies())
    }

    fun shareMovie(movieId: Long) {
        _shareBody.value = BASE_URL + "movie/" + movieId + "/"
        _shareBody.value = null
    }

    fun addToWatchLater(movieId: Long) {
        val movie = _movies.value?.first { it.id == movieId }
        movie?.let {
            movie.isInWatchLater = !movie.isInWatchLater
            dbDisposable.add(databaseManager.update(movie))
        }
    }

    fun addToFavorites(movieId: Long) {
        val movie = _movies.value?.first { it.id == movieId }
        movie?.let {
            movie.isFavorite = !movie.isFavorite
            dbDisposable.add(databaseManager.update(movie))
        }
    }

    private fun makeAPICall() {
        if (_selectedCategory.value == MoviesCategory.POPULAR || _selectedCategory.value == null) {
            _selectedCategory.value = MoviesCategory.POPULAR
            getPopularMovies()
        } else if (_selectedCategory.value == MoviesCategory.TOP_RATED) {
            _selectedCategory.value = MoviesCategory.TOP_RATED
            getTopRatedMovies()
        }
    }

    fun onPopularClick() {
        if (_selectedCategory.value == MoviesCategory.TOP_RATED) {
            _selectedCategory.value = MoviesCategory.POPULAR
            onRefresh()
        }
    }

    fun onTopRatedClick() {
        if (_selectedCategory.value == MoviesCategory.POPULAR) {
            _selectedCategory.value = MoviesCategory.TOP_RATED
            onRefresh()
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
        dbDisposable.dispose()
        super.onCleared()
    }
}