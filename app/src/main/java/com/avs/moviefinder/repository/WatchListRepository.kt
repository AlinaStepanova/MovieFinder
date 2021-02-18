package com.avs.moviefinder.repository

import com.avs.moviefinder.data.database.DatabaseManager
import com.avs.moviefinder.data.dto.Movie
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchListRepository @Inject constructor(
    private val databaseManager: DatabaseManager
) {

    private val compositeDisposable = CompositeDisposable()

    fun getWatchList() {
        compositeDisposable.add(databaseManager.getWatchLaterList())
    }

    fun updateMovie(movie: Movie) {
        compositeDisposable.add(databaseManager.update(movie))
    }

    fun clear() = compositeDisposable.clear()
}