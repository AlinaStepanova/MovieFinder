package com.avs.moviefinder.ui.movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avs.moviefinder.data.dto.Movie
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
            is Movie -> _movie.value = event
            is Locale -> openMovieDetails(_movie.value)
        }
    }

    fun openMovieDetails(movie: Movie?) {
        movie?.let {
            _movie.value?.posterPath = it.posterPath
            _movie.value?.title = it.title
            isInitiallyFavorite = it.isFavorite
            isInitiallyInWatchList = it.isInWatchLater
            movieRepository.getMovieData(it)
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