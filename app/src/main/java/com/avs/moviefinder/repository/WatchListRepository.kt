package com.avs.moviefinder.repository

import com.avs.moviefinder.data.database.DatabaseManager
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.dto.WatchList
import com.avs.moviefinder.utils.RxBus
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchListRepository @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val rxBus: RxBus
) {

    private val compositeDisposable = CompositeDisposable()

    fun getWatchList() {
        compositeDisposable.add(
            databaseManager
                .getWatchLaterList()
                .subscribe({ watchList ->
                    rxBus.send(WatchList(watchList?.sortedByDescending { it.lastTimeUpdated }))
                }, { rxBus.send(it) })
        )
    }

    fun updateMovie(movie: Movie) {
        compositeDisposable.add(databaseManager.update(movie))
    }

    fun dispose() {
        compositeDisposable.dispose()
    }
}