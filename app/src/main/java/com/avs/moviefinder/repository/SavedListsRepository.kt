package com.avs.moviefinder.repository

import com.avs.moviefinder.data.database.DatabaseManager
import com.avs.moviefinder.data.dto.Movie
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SavedListsRepository @Inject constructor(
    private val databaseManager: DatabaseManager
) {

    private val compositeDisposable = CompositeDisposable()

    fun getFavoritesList() {
        compositeDisposable.add(databaseManager.getAllFavorites())
    }

    fun getWatchList() {
        compositeDisposable.add(databaseManager.getWatchLaterList())
    }

    fun updateMovie(movie: Movie) {
        compositeDisposable.add(databaseManager.update(movie))
    }

    fun clear() = compositeDisposable.clear()
}