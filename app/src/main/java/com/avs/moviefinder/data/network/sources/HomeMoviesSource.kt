package com.avs.moviefinder.data.network.sources

import androidx.paging.PagingState
import androidx.paging.rxjava2.RxPagingSource
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.dto.MoviesResponse
import com.avs.moviefinder.data.network.ServerApi
import com.avs.moviefinder.ui.home.MoviesCategory
import com.avs.moviefinder.utils.*
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class HomeMoviesSource (
    private val serverApi: ServerApi,
    private val category: MoviesCategory,
) : RxPagingSource<Int, Movie>() {

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, Movie>> {
        val position = params.key ?: 1

        val single: Single<MoviesResponse> = when (category) {
            MoviesCategory.POPULAR -> {
                serverApi.getMovies(buildPopularMoviesUrl(position))
            }
            MoviesCategory.NOW_PLAYING -> {
                serverApi.getMovies(buildNowPlayingUrl(position))
            }
            MoviesCategory.TOP_RATED -> {
                serverApi.getMovies(buildTopRatedMoviesUrl(position))
            }
            MoviesCategory.UPCOMING -> {
                serverApi.getMovies(buildUpcomingMoviesUrl(position))
            }
        }

        return single
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