package com.avs.moviefinder.ui.movie

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avs.moviefinder.data.dto.*
import com.avs.moviefinder.repository.MovieRepository
import com.avs.moviefinder.utils.*
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
    private var _crew = MutableLiveData<List<Crew>>()
    val crew: LiveData<List<Crew>>
        get() = _crew
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

    @VisibleForTesting
    fun subscribeToEvents(event: Any?) {
        when (event) {
            is Movie -> {
                if (_movie.value?.id == 0L || _movie.value?.id == null
                    || _movie.value?.id == event.id) {
                        _movie.value = event
                }
            }
            is Credits -> {
                _cast.value = event.cast.take(CAST_REQUIRED_COUNT)
                _crew.value = getSortedCrew(event).take(CREW_REQUIRED_COUNT)
            }
            is Similar -> {
                _similarMovies.value = event.similar.take(SIMILAR_MOVIES_REQUIRED_COUNT)
            }
            is Locale -> openMovieDetails(_movie.value)
        }
    }

    private fun getSortedCrew(event: Credits): MutableList<Crew> {
        val sortedList = event.crew.sortedByDescending { it.popularity }.toMutableList()
        val director = sortedList.firstOrNull { it.department == DEPARTMENT_DIRECTING }
        director?.let {
            sortedList.remove(it)
            sortedList.add(0, it)
        }
        return sortedList
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
}