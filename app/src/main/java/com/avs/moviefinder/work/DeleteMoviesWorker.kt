package com.avs.moviefinder.work

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.avs.moviefinder.repository.BaseRepository
import com.avs.moviefinder.utils.isMovieLastUpdated2DaysAgo
import io.reactivex.Single

class DeleteMoviesWorker(appContext: Context, params: WorkerParameters) :
    RxWorker(appContext, params) {

    val repository = BaseRepository()

    override fun createWork(): Single<Result> {
        return repository.getAllMovies()
            .doOnSuccess { localMovies ->
                for (movie in localMovies) {
                    if (!movie.isFavorite && !movie.isInWatchLater
                        && isMovieLastUpdated2DaysAgo(movie.lastTimeUpdated)) {
                        repository.deleteMovie(movie)
                    }
                }
            }
            .map { Result.success() }
            .onErrorReturn { Result.failure() }
    }

    companion object {
        const val WORK_NAME = "DeleteMoviesWorker"
    }

}