package com.avs.moviefinder.ui.movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avs.moviefinder.data.database.DatabaseManager
import com.avs.moviefinder.data.network.ServerApi
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.utils.BASE_URL
import com.avs.moviefinder.utils.RxBus
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class MovieViewModel @Inject constructor(
    private val serverApi: ServerApi,
    private val databaseManager: DatabaseManager,
    rxBus: RxBus
) : ViewModel() {
    private var _movie = MutableLiveData<Movie?>()
    val movie: LiveData<Movie?>
        get() = _movie
    private var _shareBody = MutableLiveData<String?>()
    val shareBody: LiveData<String?>
        get() = _shareBody
    private var rxBusDisposable: Disposable? = null
    private var apiDisposable: Disposable? = null
    private var extrasMovie = Movie()

    init {
        rxBusDisposable = rxBus.events.subscribe { event -> handleServerResponse(event) }
    }

    private fun handleServerResponse(event: Any?) {
        if (event is Movie) {
            event.isFavorite = extrasMovie.isFavorite
            event.isInWatchLater = extrasMovie.isInWatchLater
            _movie.value = event
            //databaseManager.update(_movie.value as Movie)
            // todo update database
        } else if (event is Throwable) {
            // todo add error handling
        }
    }

    fun shareMovie() {
        movie.value?.let {
            _shareBody.value = BASE_URL + "movie/" + _movie.value?.id + "/"
        }
        _shareBody.value = null
    }

    fun openMovieDetails(movie: Movie?) {
        if (movie != null) {
            extrasMovie = movie
            apiDisposable?.dispose()
            apiDisposable = serverApi.getMovieById(movie.id)
        }
    }

    override fun onCleared() {
        apiDisposable?.dispose()
        rxBusDisposable?.dispose()
        super.onCleared()
    }


}