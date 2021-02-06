package com.avs.moviefinder.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avs.moviefinder.data.dto.FavoritesList
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.repository.FavoritesRepository
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

class FavoritesViewModel @Inject constructor(
    rxBus: RxBus,
    private val favoritesRepository: FavoritesRepository
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
    private var _isInserted = MutableLiveData<Boolean?>()
    val isInserted: LiveData<Boolean?>
        get() = _isInserted
    private var removedMovie: Movie? = null
    private val dbDisposable = CompositeDisposable()
    private var rxBusDisposable: Disposable? = null
    private var timer: Disposable? = null

    init {
        rxBusDisposable = rxBus.events.subscribe { event -> subscribeToEvents(event) }
        _isProgressVisible.value = true
        getFavorites()
    }

    override fun onCleared() {
        dbDisposable.dispose()
        rxBusDisposable?.dispose()
        favoritesRepository.dispose()
        timer?.dispose()
        super.onCleared()
    }

    private fun subscribeToEvents(event: Any) {
        when (event) {
            is FavoritesList -> {
                _isProgressVisible.value = false
                if (event.movies != null && event.movies != _movies.value) {
                    _movies.value = ArrayList(event.movies)
                }
            }
            is Movie -> {
                _movies.value?.let { list ->
                    val fetchedMovie = list.firstOrNull { it.id == event.id }
                    fetchedMovie?.let {
                        disposeDeletingDependencies()
                        val updatedMovieIndex = list.indexOf(it)
                        if (updatedMovieIndex != -1) {
                            _updateMovieIndex.value = updatedMovieIndex
                            if (!event.isFavorite) {
                                _isInserted.value = false
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

    fun getFavorites() = favoritesRepository.getFavorites()

    fun undoRemovingMovie() {
        if (removedMovie != null && _updateMovieIndex.value != null) {
            _movies.value?.let { movies ->
                movies.add(_updateMovieIndex.value!!, removedMovie!!)
                addFavorites(removedMovie!!.id)
                _isInserted.value = true
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
            if (isInWatchLater) {
                it.lastTimeUpdated = System.currentTimeMillis()
            }
            favoritesRepository.updateMovie(movie)
        }
    }

    fun addFavorites(movieId: Long) {
        val movie = _movies.value?.firstOrNull { it.id == movieId }
        movie?.let {
            val isFavorite = !it.isFavorite
            it.isFavorite = isFavorite
            if (isFavorite && removedMovie == null) {
                it.lastTimeUpdated = System.currentTimeMillis()
            }
            favoritesRepository.updateMovie(movie)
        }
    }

}