package com.avs.moviefinder.data.database

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.observable
import com.avs.moviefinder.BuildConfig
import com.avs.moviefinder.data.dto.FavoritesList
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.dto.WatchList
import com.avs.moviefinder.utils.RxBus
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
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
            .subscribe({ rxBus.send(movie) }, { handleError(it) })
    }

    fun insertMovies(movies: List<Movie>): Disposable {
        return dataSource.insertAll(movies)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ }, { handleError(it) })
    }

    fun getAllMovies(): Single<List<Movie>> {
        return dataSource.getAllEntries()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun update(movie: Movie): Disposable {
        return dataSource.update(movie)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ rxBus.send(movie) }, { handleError(it) })
    }

    fun delete(movie: Movie): Disposable {
        return Single
            .fromCallable { dataSource.delete(movie) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ }, { handleError(it) })
    }

    fun getMovieByIdAsSingle(id: Long): Single<Movie?> {
        return dataSource.get(id)
    }

    private fun getFavoritesMoviesPage(): Observable<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = true,
                prefetchDistance = 2,
                initialLoadSize = 5
            ),
            pagingSourceFactory = { dataSource.getFavoritesList() }
        ).observable
    }

    fun getAllFavorites(): Disposable {
        return getFavoritesMoviesPage()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ it?.let { favorites -> rxBus.send(FavoritesList(favorites)) } }, { handleError(it) })
    }

    fun getWatchLaterList(): Disposable {
        return dataSource.getWatchLaterList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ it?.let { watchList -> rxBus.send(WatchList(watchList)) } }, { handleError(it) })
    }

    private fun handleError(error: Throwable?) {
        if (BuildConfig.DEBUG) {
            if (error != null) {
                rxBus.send(error)
            }
            Log.e(this.javaClass.simpleName, error.toString())
        }
    }
}