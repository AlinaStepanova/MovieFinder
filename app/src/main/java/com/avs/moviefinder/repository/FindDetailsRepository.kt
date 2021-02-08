package com.avs.moviefinder.repository

import com.avs.moviefinder.data.database.DatabaseManager
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.network.ServerApi
import com.avs.moviefinder.utils.RxBus
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FindDetailsRepository @Inject constructor(
    private val serverApi: ServerApi,
    private val databaseManager: DatabaseManager,
    private val rxBus: RxBus
) {
    private val compositeDisposable = CompositeDisposable()

    private fun deleteMovie(movie: Movie) = compositeDisposable.add(databaseManager.delete(movie))

    fun insertMovie(movie: Movie) = compositeDisposable.add(databaseManager.insertMovie(movie))

    fun updateMovie(movie: Movie) = compositeDisposable.add(databaseManager.update(movie))

    fun clear() = compositeDisposable.clear()
}