package com.avs.moviefinder.work

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.avs.moviefinder.repository.BaseRepository
import io.reactivex.Single

class DeleteMoviesWorker(appContext: Context, params: WorkerParameters) :
    RxWorker(appContext, params) {

    val repository = BaseRepository()

    override fun createWork(): Single<Result> {
        return repository.getAllMovies()
            .doOnSuccess { localMovies ->
                for (movie in localMovies) {
                    if (!movie.isFavorite && !movie.isInWatchLater) {
                        repository.deleteMovie(movie)
                    }
                }
            }
            .map { Result.success() }
            .onErrorReturn { Result.failure() }
    }

}