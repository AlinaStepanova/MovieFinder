package com.avs.moviefinder.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avs.moviefinder.data.database.DatabaseManager
import com.avs.moviefinder.data.dto.Movie
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
    private val dbDisposable = CompositeDisposable()
    private var rxBusDisposable: Disposable? = null

    init {
        dbDisposable.add(databaseManager.getAllFavorites())
        rxBusDisposable = rxBus.events.subscribe { event -> handleServerResponse(event) }
    }

    private fun handleServerResponse(event: Any?) {
        if (event is List<*>) {
            _movies.value = event as List<Movie>
        }
    }

    override fun onCleared() {
        dbDisposable.dispose()
        rxBusDisposable?.dispose()
        super.onCleared()
    }

    fun shareMovie(movieId: Long) {}

    fun addToWatchLater(movieId: Long) {}

    fun addToWatched(movieId: Long) {}

}