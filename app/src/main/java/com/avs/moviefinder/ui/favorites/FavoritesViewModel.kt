package com.avs.moviefinder.ui.favorites

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avs.moviefinder.data.database.DatabaseManager
import com.avs.moviefinder.data.dto.FavoritesList
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.utils.BASE_URL
import com.avs.moviefinder.utils.RxBus
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
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
    private val dbDisposable = CompositeDisposable()
    private var rxBusDisposable: Disposable? = null

    init {

        rxBusDisposable = rxBus.events.subscribe { event -> handleDBResponse(event) }
    }

    private fun handleDBResponse(event: Any) {
        when (event) {
            is FavoritesList -> {
                _isProgressVisible.value = false
                if (event.movies != null && event.movies != _movies.value) {
                    _movies.value = (event.movies as ArrayList<Movie>)
                }
            }
            is Movie -> {
                _movies.value?.let {
                    val fetchedMovie = _movies.value?.firstOrNull { it.id == event.id }
                    fetchedMovie?.let {
                        val updatedMovieIndex = _movies.value!!.indexOf(fetchedMovie)
                        if (updatedMovieIndex != -1) {
                            _updateMovieIndex.value = updatedMovieIndex
                            if (!event.isFavorite) {
                                _isInserted.value = false
                                _movies.value!!.removeAt(updatedMovieIndex)
                            } else {
                                _movies.value!![updatedMovieIndex] = event
                            }
                        }
                    }
                }
            }
        }
    }

    fun fetchFavoriteMovies() {
        _isProgressVisible.value = true
        dbDisposable.add(databaseManager.getAllFavorites())
    }

    override fun onCleared() {
        dbDisposable.dispose()
        rxBusDisposable?.dispose()
        super.onCleared()
    }

    fun shareMovie(movieId: Long) {
        _shareBody.value = BASE_URL + "movie/" + movieId + "/"
        _shareBody.value = null
    }

    fun addToWatchLater(movieId: Long) {
        val movie = _movies.value?.firstOrNull { it.id == movieId }
        movie?.let {
            movie.isInWatchLater = !movie.isInWatchLater
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