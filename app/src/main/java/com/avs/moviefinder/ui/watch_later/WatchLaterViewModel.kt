package com.avs.moviefinder.ui.watch_later

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.dto.WatchList
import com.avs.moviefinder.repository.SavedListsRepository
import com.avs.moviefinder.utils.LONG_DURATION_MS
import com.avs.moviefinder.utils.RxBus
import com.avs.moviefinder.utils.buildShareLink
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WatchLaterViewModel @Inject constructor(
    rxBus: RxBus,
    private val repository: SavedListsRepository
) : ViewModel() {

    private var _movies = MutableLiveData<ArrayList<Movie>>()
    val movies: LiveData<ArrayList<Movie>>
        get() = _movies
    private var _isProgressVisible = MutableLiveData<Boolean>()
    val isProgressVisible: LiveData<Boolean>
        get() = _isProgressVisible
    private var _shareBody = MutableLiveData<String?>()
    val shareBody: LiveData<String?>
        get() = _shareBody
    private var _updateMovieIndex = MutableLiveData<Int?>()
    val updateMovieIndex: LiveData<Int?>
        get() = _updateMovieIndex
    private var _isInserted = MutableLiveData<Pair<Boolean, String>?>()
    val isInserted: LiveData<Pair<Boolean, String>?>
        get() = _isInserted
    private var removedMovie: Movie? = null
    private val compositeDisposable = CompositeDisposable()
    private var timer: Disposable? = null

    init {
        compositeDisposable.add(rxBus.events.subscribe { event -> subscribeToEvents(event) })
        _isProgressVisible.value = true
        getWatchLaterMovies()
    }

    private fun subscribeToEvents(event: Any) {
        when (event) {
            is WatchList -> {
                _isProgressVisible.value = false
                if (event.movies != null && event.movies != _movies.value) {
                    _movies.value = ArrayList(event.movies)
                }
            }
            is Movie -> {
                _movies.value?.let { list ->
                    val fetchedMovie = _movies.value?.firstOrNull { it.id == event.id }
                    fetchedMovie?.let { movie ->
                        disposeDeletingDependencies()
                        val updatedMovieIndex = list.indexOf(movie)
                        if (updatedMovieIndex != -1) {
                            _updateMovieIndex.value = updatedMovieIndex
                            if (!event.isInWatchLater) {
                                _isInserted.value = Pair(false, movie.title ?: "")
                                removedMovie = list[updatedMovieIndex]
                                list.removeAt(updatedMovieIndex)
                                startCountdown()
                            } else {
                                list[updatedMovieIndex] = event
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        compositeDisposable.clear()
        repository.clear()
        timer?.dispose()
        super.onCleared()
    }

    private fun startCountdown() {
        timer = Single.timer(LONG_DURATION_MS, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { disposeDeletingDependencies() }
            .subscribe()
    }

    private fun disposeDeletingDependencies() {
        timer?.dispose()
        _isInserted.value = null
        _updateMovieIndex.value = null
        removedMovie = null
    }

    fun getWatchLaterMovies() = repository.getWatchList()

    fun undoRemovingMovie() {
        if (removedMovie != null && _updateMovieIndex.value != null) {
            _movies.value?.let { movies ->
                movies.add(_updateMovieIndex.value!!, removedMovie!!)
                addToWatchLater(removedMovie!!.id)
                _isInserted.value = Pair(true, "")
                disposeDeletingDependencies()
            }
        }
    }

    fun shareMovie(movieId: Long) {
        _shareBody.value = buildShareLink(movieId)
        _shareBody.value = null
    }

    fun addToWatchLater(movieId: Long) {
        val movie = _movies.value?.firstOrNull { it.id == movieId }
        movie?.let {
            val isInWatchLater = !it.isInWatchLater
            it.isInWatchLater = isInWatchLater
            if (isInWatchLater && removedMovie == null) {
                it.lastTimeUpdated = System.currentTimeMillis()
            }
            repository.updateMovie(movie)
        }
    }

    fun addFavorites(movieId: Long) {
        val movie = _movies.value?.firstOrNull { it.id == movieId }
        movie?.let {
            val isFavorite = !it.isFavorite
            it.isFavorite = isFavorite
            if (isFavorite) {
                it.lastTimeUpdated = System.currentTimeMillis()
            }
            repository.updateMovie(movie)
        }
    }
}