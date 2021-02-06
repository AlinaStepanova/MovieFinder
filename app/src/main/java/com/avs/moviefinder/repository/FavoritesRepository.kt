package com.avs.moviefinder.repository

import com.avs.moviefinder.data.database.DatabaseManager
import com.avs.moviefinder.data.dto.Movie
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepository @Inject constructor(
    private val databaseManager: DatabaseManager
) {

    private val compositeDisposable = CompositeDisposable()

    fun getFavorites() {
        compositeDisposable.add(databaseManager.getAllFavorites())
    }

    fun updateMovie(movie: Movie) {
        compositeDisposable.add(databaseManager.update(movie))
    }

    fun dispose() {
        compositeDisposable.dispose()
    }
}