package com.avs.moviefinder.ui.movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avs.moviefinder.data.dto.*
import com.avs.moviefinder.repository.MovieRepository
import com.avs.moviefinder.utils.RxBus
import com.avs.moviefinder.utils.buildShareLink
import io.reactivex.disposables.Disposable
import java.util.*
import javax.inject.Inject

class MovieViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    rxBus: RxBus
) : ViewModel() {
    private var _movie = MutableLiveData<Movie?>()
    val movie: LiveData<Movie?>
        get() = _movie
    private var _cast = MutableLiveData<List<Cast>>()
    val cast: LiveData<List<Cast>>
        get() = _cast
    private var _similarMovies = MutableLiveData<List<Result>>()
    val similarMovies: LiveData<List<Result>>
        get() = _similarMovies
    private var _shareBody = MutableLiveData<String?>()
    val shareBody: LiveData<String?>
        get() = _shareBody
    private var rxBusDisposable: Disposable
    private var isInitiallyFavorite = false
    private var isInitiallyInWatchList = false

    init {
        rxBusDisposable = rxBus.events.subscribe { event -> subscribeToEvents(event) }
    }

    override fun onCleared() {
        rxBusDisposable.dispose()
        movieRepository.clear()
        super.onCleared()
    }

    private fun subscribeToEvents(event: Any?) {
        when (event) {
            is Movie -> {
                if (_movie.value?.id == 0L || _movie.value?.id == null
                    || _movie.value?.id == event.id) {
                        _movie.value = event
                }
            }
            is Credits -> {
                _cast.value = event.cast.take(16)
            }
            is Similar -> {
                _similarMovies.value = event.similar.take(10)
            }
            is Locale -> openMovieDetails(_movie.value)
        }
    }

    fun openMovieDetails(movie: Movie?) {
        if (movie != null) {
            _movie.value?.id = movie.id
            _movie.value?.posterPath = movie.posterPath
            _movie.value?.title = movie.title
            isInitiallyFavorite = movie.isFavorite
            isInitiallyInWatchList = movie.isInWatchLater
            movieRepository.getMovieData(movie)
        }
    }

    fun addToWatchLater() {
        _movie.value?.let {
            val isInWatchLater = !it.isInWatchLater
            it.isInWatchLater = isInWatchLater
            if (isInWatchLater) {
                it.lastTimeUpdated = System.currentTimeMillis()
            }
            movieRepository.insertMovie(it)
        }
    }

    fun addToFavorites() {
        _movie.value?.let {
            val isFavorite = !it.isFavorite
            it.isFavorite = isFavorite
            if (isFavorite) {
                it.lastTimeUpdated = System.currentTimeMillis()
            }
            movieRepository.insertMovie(it)
        }
    }

    fun shareMovie() {
        movie.value?.let {
            _shareBody.value = buildShareLink(it.id)
        }
        _shareBody.value = null
    }

    fun isInitialMovieUpdated(): Boolean {
        return _movie.value?.isFavorite != isInitiallyFavorite
                || _movie.value?.isInWatchLater != isInitiallyInWatchList
    }
}