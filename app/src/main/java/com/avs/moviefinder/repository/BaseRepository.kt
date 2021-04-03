package com.avs.moviefinder.repository

import com.avs.moviefinder.data.database.DatabaseManager
import com.avs.moviefinder.data.dto.Movie
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class BaseRepository {

    @Inject
    protected lateinit var databaseManager: DatabaseManager

    protected val compositeDisposable = CompositeDisposable()

    open fun insertMovie(movie: Movie) {
        compositeDisposable.add(databaseManager.insertMovie(movie))
    }

    fun updateMovie(movie: Movie) = compositeDisposable.add(databaseManager.update(movie))

    fun deleteMovie(movie: Movie) = compositeDisposable.add(databaseManager.delete(movie))

    fun clear() = compositeDisposable.clear()

    fun getAllMovies(): Single<List<Movie>> {
        return databaseManager.getAllMovies()
    }
}