package com.avs.moviefinder.ui.watch_later

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
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

    private var _movies = MutableLiveData<PagingData<Movie>>()
    val movies: LiveData<PagingData<Movie>>
        get() = _movies
    private var _isProgressVisible = MutableLiveData<Boolean>()
    val isProgressVisible: LiveData<Boolean>
        get() = _isProgressVisible
    private var _shareBody = MutableLiveData<String?>()
    val shareBody: LiveData<String?>
        get() = _shareBody
    private var _removedMovie = MutableLiveData<Movie?>()
    val removedMovie: LiveData<Movie?>
        get() = _removedMovie
    private val compositeDisposable = CompositeDisposable()
    private var timer: Disposable? = null

    init {
        compositeDisposable.add(rxBus.events.subscribe { event -> subscribeToEvents(event) })
        _isProgressVisible.value = true
    }

    private fun subscribeToEvents(event: Any) {
        when (event) {
            is WatchList -> {
                _isProgressVisible.value = false
                _movies.value = event.movies
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
        _removedMovie.value = null
    }

    fun getWatchLaterMovies() = repository.getWatchList()

    fun undoRemovingMovie() {
        _removedMovie.value?.let {
            disposeDeletingDependencies()
            addToWatchLater(it)
        }
    }

    fun shareMovie(movieId: Long) {
        _shareBody.value = buildShareLink(movieId)
        _shareBody.value = null
    }

    fun addToWatchLater(movie: Movie) {
        val isInWatchLater = !movie.isInWatchLater
        val updatedMovie = movie.copy()
        updatedMovie.isInWatchLater = isInWatchLater
        if (!updatedMovie.isInWatchLater) {
            disposeDeletingDependencies()
            _removedMovie.value = updatedMovie
            startCountdown()
        }
        repository.updateMovie(updatedMovie)
    }

    fun addFavorites(movie: Movie) {
        val isFavorite = !movie.isFavorite
        val updatedMovie = movie.copy()
        updatedMovie.isFavorite = isFavorite
        repository.updateMovie(updatedMovie)
    }
}