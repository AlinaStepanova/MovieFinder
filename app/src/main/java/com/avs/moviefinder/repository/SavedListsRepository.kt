package com.avs.moviefinder.repository

import androidx.paging.rxjava2.cachedIn
import com.avs.moviefinder.data.dto.FavoritesList
import com.avs.moviefinder.data.dto.WatchList
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class SavedListsRepository @Inject constructor() : BaseRepository() {

    fun getFavoritesList(scope: CoroutineScope) {
        compositeDisposable.add(getAllFavorites(scope))
    }

    fun getWatchList(scope: CoroutineScope) {
        compositeDisposable.add(getWatchLaterList(scope))
    }

    private fun getAllFavorites(scope: CoroutineScope): Disposable {
        return databaseManager.getFavoritesMoviesPage()
            .cachedIn(scope)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ it?.let { favorites -> databaseManager.rxBus.send(FavoritesList(favorites)) } }, { databaseManager.handleError(it) })
    }

    private fun getWatchLaterList(scope: CoroutineScope): Disposable {
        return databaseManager.getWatchLaterMoviesPage()
            .cachedIn(scope)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ it?.let { watchList -> databaseManager.rxBus.send(WatchList(watchList)) } }, { databaseManager.handleError(it) })
    }
}