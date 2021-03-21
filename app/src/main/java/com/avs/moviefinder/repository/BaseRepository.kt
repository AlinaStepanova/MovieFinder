package com.avs.moviefinder.repository

import com.avs.moviefinder.data.database.DatabaseManager
import com.avs.moviefinder.data.dto.Movie
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

abstract class BaseRepository {

    @Inject
    protected lateinit var databaseManager: DatabaseManager

    protected val compositeDisposable = CompositeDisposable()

    open fun insertMovie(movie: Movie) {
        compositeDisposable.add(databaseManager.insertMovie(movie))
    }

    fun updateMovie(movie: Movie) = compositeDisposable.add(databaseManager.update(movie))

    fun deleteMovie(movie: Movie) = compositeDisposable.add(databaseManager.delete(movie))

    fun clear() = compositeDisposable.clear()
}