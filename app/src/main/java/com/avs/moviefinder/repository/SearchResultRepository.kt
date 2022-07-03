package com.avs.moviefinder.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.cachedIn
import androidx.paging.rxjava2.observable
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.dto.PagingDataList
import com.avs.moviefinder.data.network.ServerApi
import com.avs.moviefinder.data.network.sources.SearchMoviesSource
import com.avs.moviefinder.utils.PAGE_SIZE
import com.avs.moviefinder.utils.PREFETCH_DISTANCE
import com.avs.moviefinder.utils.RxBus
import io.reactivex.Observable
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

    private fun getPagedData(query: String): Observable<PagingData<Movie>> {
        val pager = Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = true,
                maxSize = PREFETCH_DISTANCE * 2 + PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                initialLoadSize = PAGE_SIZE * 2
            ),
            pagingSourceFactory = { SearchMoviesSource(serverApi, query) }
        )
        return pager.observable
    }
}