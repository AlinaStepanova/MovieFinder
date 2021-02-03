package com.avs.moviefinder.ui.home

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.dto.MoviesAPIFilter
import com.avs.moviefinder.data.dto.MoviesDBFilter
import com.avs.moviefinder.data.network.ErrorType
import com.avs.moviefinder.repository.HomeRepository
import com.avs.moviefinder.ui.MOVIE_EXTRA_TAG
import com.avs.moviefinder.utils.IS_MOVIE_UPDATED_EXTRA
import com.avs.moviefinder.utils.RxBus
import com.avs.moviefinder.utils.buildMowPlayingUrl
import com.avs.moviefinder.utils.buildShareLink
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    rxBus: RxBus,
    private val homeRepository: HomeRepository
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
    private val compositeDisposable = CompositeDisposable()
    private var _selectedCategory = MutableLiveData<MoviesCategory>()
    val selectedCategory: LiveData<MoviesCategory>
        get() = _selectedCategory
    private var _updateMovieIndex = MutableLiveData<Int?>()
    val updateMovieIndex: LiveData<Int?>
        get() = _updateMovieIndex
    private var _isBackOnline = MutableLiveData<Boolean?>()
    val isBackOnline: LiveData<Boolean?>
        get() = _isBackOnline

    init {
        compositeDisposable.addAll(
            rxBus.events.subscribe { event -> handleServerResponse(event) },
            homeRepository.getAllMovies()
        )
    }

    override fun onCleared() {
        apiDisposable?.dispose()
        compositeDisposable.dispose()
        homeRepository.dispose()
        super.onCleared()
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
                _errorType.value = if (event.movies.isEmpty()) ErrorType.NO_RESULTS else null
                val movies = event.movies
                homeRepository.combineServerAndDatabaseData(_moviesDB.value, movies)
                if (movies.isEmpty() || movies.first.id != 0L) {
                    movies.addFirst(Movie())
                }
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
            is Throwable -> {
                _isProgressVisible.value = false
                _isLoading.value = false
                _movies.value = LinkedList()
                _errorType.value = ErrorType.NETWORK
            }
        }
    }

    private fun makeAPICall() {
        if (_selectedCategory.value == MoviesCategory.POPULAR || _selectedCategory.value == null) {
            getPopularMovies()
        } else if (_selectedCategory.value == MoviesCategory.TOP_RATED) {
            getTopRatedMovies()
        } else if (_selectedCategory.value == MoviesCategory.NOW_PLAYING) {
            getNowPlayingMovies()
        }
    }

    private fun getPopularMovies() {
        disposeValues()
        apiDisposable = homeRepository.getPopularMovies()
    }

    private fun getTopRatedMovies() {
        disposeValues()
        apiDisposable = homeRepository.getTopRatedMovies()
    }

    private fun getNowPlayingMovies() {
        disposeValues()
        val url = buildMowPlayingUrl()
        if (url.isNotEmpty()) {
            apiDisposable = homeRepository.getNowPlayingMovies(url)
        }
    }

    private fun disposeValues() {
        _errorType.value = null
        apiDisposable?.dispose()
    }

    fun onRefresh() {
        compositeDisposable.add(homeRepository.getAllMovies())
    }

    fun shareMovie(movieId: Long) {
        _shareBody.value = buildShareLink(movieId)
        _shareBody.value = null
    }

    fun addToWatchLater(movieId: Long) {
        val movie = _movies.value?.firstOrNull { it.id == movieId }
        movie?.let {
            it.isInWatchLater = !it.isInWatchLater
            it.lastTimeUpdated = System.currentTimeMillis()
            compositeDisposable.add(homeRepository.insertMovie(it))
        }
    }

    fun addToFavorites(movieId: Long) {
        val movie = _movies.value?.firstOrNull { it.id == movieId }
        movie?.let {
            it.isFavorite = !it.isFavorite
            it.lastTimeUpdated = System.currentTimeMillis()
            compositeDisposable.add(homeRepository.insertMovie(it))
        }
    }

    fun onPopularClick() {
        if (_selectedCategory.value != MoviesCategory.POPULAR) {
            _selectedCategory.value = MoviesCategory.POPULAR
            onRefresh()
        }
    }

    fun onTopRatedClick() {
        if (_selectedCategory.value != MoviesCategory.TOP_RATED) {
            _selectedCategory.value = MoviesCategory.TOP_RATED
            onRefresh()
        }
    }

    fun onNowPlayingClick() {
        if (_selectedCategory.value != MoviesCategory.NOW_PLAYING) {
            _selectedCategory.value = MoviesCategory.NOW_PLAYING
            onRefresh()
        }
    }

    fun reactOnNetworkChangeState(isActive: Boolean) {
        if (isActive && _errorType.value == ErrorType.NETWORK) {
            onRefresh()
            _isBackOnline.value = true
            _isBackOnline.value = null
        }
    }

    fun handleOnActivityResult(resultIntent: Intent) {
        val isMovieUpdated = resultIntent.getBooleanExtra(IS_MOVIE_UPDATED_EXTRA, false)
        if (isMovieUpdated) {
            val updatedMovie = resultIntent.getParcelableExtra<Movie>(MOVIE_EXTRA_TAG)
            if (updatedMovie != null && updatedMovie.id > 0) {
                updatedMovie.lastTimeUpdated = System.currentTimeMillis()
                compositeDisposable.add(homeRepository.updateMovie(updatedMovie))
            }
        }
    }
}