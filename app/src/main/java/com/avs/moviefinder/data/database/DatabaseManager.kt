package com.avs.moviefinder.data.database

import android.util.Log
import com.avs.moviefinder.BuildConfig
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.dto.MoviesDBFilter
import com.avs.moviefinder.utils.RxBus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseManager @Inject constructor(
    private val rxBus: RxBus,
    private val dataSource: MovieDatabaseDao
) {

    fun insertMovie(movie: Movie): Disposable {
        return dataSource.insert(movie)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ }, { handleError(it) })
    }

    fun insertMovies(movies: List<Movie>): Disposable {
        return dataSource.insertAll(movies)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ }, { handleError(it) })
    }

    fun getAllMovies(): Disposable {
        return dataSource.getAllEntries()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ rxBus.send(MoviesDBFilter(it)) }, { handleError(it) })
    }

    fun update(movie: Movie): Disposable {
        return dataSource.update(movie)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ rxBus.send(movie) }, { handleError(it) })
    }

    fun getAllFavorites(): Disposable {
        return dataSource.getFavoritesList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ it?.let { it1 -> rxBus.send(it1) } }, { handleError(it) })
    }

    private fun readSuccessMessage(items: Any) {
        Log.d(this.javaClass.simpleName, "Inserted successfully $items")
    }

    private fun handleError(error: Throwable?) {
        if (BuildConfig.DEBUG) {
            if (error != null) {
                rxBus.send(error)
            }
            Log.d(this.javaClass.simpleName, error.toString())
        }
    }
}