package com.avs.moviefinder.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avs.moviefinder.data.database.DatabaseManager
import com.avs.moviefinder.data.dto.FavoritesList
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.utils.BASE_URL
import com.avs.moviefinder.utils.RxBus
import com.avs.moviefinder.utils.LONG_DURATION_MS
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FavoritesViewModel @Inject constructor(
    rxBus: RxBus,
    private val databaseManager: DatabaseManager
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
        rxBusDisposable = rxBus.events.subscribe { event -> handleDBResponse(event) }
    }

    override fun onCleared() {
        dbDisposable.dispose()
        rxBusDisposable?.dispose()
        timer?.dispose()
        super.onCleared()
    }

    private fun handleDBResponse(event: Any) {
        when (event) {
            is FavoritesList -> {
                _isProgressVisible.value = false
                if (event.movies != null && event.movies != _movies.value) {
                    _movies.value = ArrayList(event.movies.sortedByDescending { it.lastTimeUpdated })
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
                        } // todo add new movie to the list
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

    fun fetchFavoriteMovies() {
        _isProgressVisible.value = true
        dbDisposable.add(databaseManager.getAllFavorites())
    }

    fun undoRemovingMovie() {
        if (removedMovie != null && _updateMovieIndex.value != null) {
            _movies.value?.let {
                it.add(_updateMovieIndex.value!!, removedMovie!!)
                addFavorites(removedMovie!!.id)
                _isInserted.value = true
                disposeDeletingDependencies()
            }
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
            dbDisposable.add(databaseManager.update(movie))
        }
    }

    fun addFavorites(movieId: Long) {
        val movie = _movies.value?.firstOrNull { it.id == movieId }
        movie?.let {
            movie.isFavorite = !movie.isFavorite
            dbDisposable.add(databaseManager.update(movie))
        }
    }

}