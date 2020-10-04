package com.avs.moviefinder.ui.favorites

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

    private var _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>>
        get() = _movies
    private var _isProgressVisible = MutableLiveData<Boolean>()
    val isProgressVisible: LiveData<Boolean>
        get() = _isProgressVisible
    private var _shareBody = MutableLiveData<String?>()
    val shareBody: LiveData<String?>
        get() = _shareBody
    private var _updateMovie = MutableLiveData<Int?>()
    val updateMovie: LiveData<Int?>
        get() = _updateMovie
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
                    _movies.value = event.movies!!
                }
            }
            is Movie -> {
                _movies.value?.let {
                    val updatedMovieIndex = _movies.value!!.indexOf(event)
                    if (updatedMovieIndex != -1) {
                        _updateMovie.value = updatedMovieIndex
                        _updateMovie.value = null
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
        val movie = _movies.value?.first { it.id == movieId }
        movie?.let {
            movie.isInWatchLater = !movie.isInWatchLater
            dbDisposable.add(databaseManager.update(movie))
        }
    }

    fun addFavorites(movieId: Long) {}

}