package com.avs.moviefinder.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.cachedIn
import androidx.paging.rxjava2.flowable
import com.avs.moviefinder.data.MoviesSource
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.dto.PagingDataList
import com.avs.moviefinder.data.network.ServerApi
import com.avs.moviefinder.utils.RxBus
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class SearchResultRepository @Inject constructor(
    private val serverApi: ServerApi,
    private val rxBus: RxBus
): BaseRepository() {

    fun getAllPagedMovies(query: String, scope: CoroutineScope) {
        compositeDisposable.add(getPagedData(query)
            .cachedIn(scope)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it?.let { pagingData ->
                    rxBus.send(PagingDataList(pagingData))
                }
            }, { databaseManager.handleError(it)})
        )
    }

    private fun getPagedData(query: String): Flowable<PagingData<Movie>> {
        val c =  Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = true,
                maxSize = 30,
                prefetchDistance = 5,
                initialLoadSize = 40),
            pagingSourceFactory = { MoviesSource(serverApi, query) }
        )
        return c.flowable
    }
}