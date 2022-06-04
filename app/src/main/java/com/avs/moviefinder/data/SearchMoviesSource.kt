package com.avs.moviefinder.data

import androidx.paging.PagingState
import androidx.paging.rxjava2.RxPagingSource
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.dto.MoviesResponse
import com.avs.moviefinder.data.network.ServerApi
import com.avs.moviefinder.utils.EMPTY_RESULT_MESSAGE
import com.avs.moviefinder.utils.buildMovieByNameUrl
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class SearchMoviesSource (
    private val serverApi: ServerApi,
    private val query: String,
) : RxPagingSource<Int, Movie>() {

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, Movie>> {
        val position = params.key ?: 1

        val url = buildMovieByNameUrl(query, position)

        return serverApi.getMovieByName(url)
            .subscribeOn(Schedulers.io())
            .map { toLoadResult(it, position) }
            .onErrorReturn { LoadResult.Error(it) }
    }

    private fun toLoadResult(data: MoviesResponse, position: Int): LoadResult<Int, Movie> {
        return if (data.results.isEmpty()) {
            LoadResult.Error(Throwable(EMPTY_RESULT_MESSAGE))
        } else {
            LoadResult.Page(
                data = data.results,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (position == data.total) null else position + 1
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { state.closestItemToPosition(it)?.id?.toInt() }
    }
}