package com.avs.moviefinder.repository

import javax.inject.Inject

class SavedListsRepository @Inject constructor() : BaseRepository() {

    fun getFavoritesList() {
        compositeDisposable.add(databaseManager.getAllFavorites())
    }

    fun getWatchList() {
        compositeDisposable.add(databaseManager.getWatchLaterList())
    }
}