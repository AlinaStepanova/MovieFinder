package com.avs.moviefinder.ui.movie

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avs.moviefinder.data.database.DatabaseManager
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.network.ServerApi
import com.avs.moviefinder.utils.BASE_URL
import com.avs.moviefinder.utils.RxBus
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
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
    private val dbDisposable = CompositeDisposable()

    init {
        rxBusDisposable = rxBus.events.subscribe { event -> handleServerResponse(event) }
    }

    private fun handleServerResponse(event: Any?) {
        if (event is Movie) {
            _movie.value = event
        } else if (event is Throwable) {
            Log.d(this::class.java.name, event.toString())
        }
    }

    fun addToWatchLater() {
        _movie.value?.let {
            val isisInWatchLater = !it.isInWatchLater
            it.isInWatchLater = isisInWatchLater
            dbDisposable.add(databaseManager.update(it))
        }
    }

    fun addToFavorites() {
        _movie.value?.let {
            val isFavorite = !it.isFavorite
            it.isFavorite = isFavorite
            dbDisposable.add(databaseManager.update(it))
        }
    }

    fun shareMovie() {
        movie.value?.let {
            _shareBody.value = BASE_URL + "movie/" + it.id + "/"
        }
        _shareBody.value = null
    }

    fun openMovieDetails(movie: Movie?) {
        if (movie != null) {
            extrasMovie = movie
            apiDisposable?.dispose()
            apiDisposable = Single.zip(
                serverApi.callMovieById(movie.id),
                databaseManager.getById(movie.id)
            ) { apiMovie, dbMovie -> combineTwoMovies(apiMovie, dbMovie) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ databaseManager.update(extrasMovie) }, {})
        }
    }

    private fun combineTwoMovies(apiMovie: Movie?, dbMovie: Movie?) {
        apiMovie?.let {
            extrasMovie = it
        }
        dbMovie?.let {
            extrasMovie.isInWatchLater = it.isInWatchLater
            extrasMovie.isFavorite = it.isFavorite
        }
    }

    override fun onCleared() {
        apiDisposable?.dispose()
        rxBusDisposable?.dispose()
        super.onCleared()
    }


}