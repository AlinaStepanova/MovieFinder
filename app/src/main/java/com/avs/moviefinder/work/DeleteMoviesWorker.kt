package com.avs.moviefinder.work

import android.content.Context
import android.util.Log
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.avs.moviefinder.data.database.DatabaseManager
import com.avs.moviefinder.utils.isMovieLastUpdated2DaysAgo
import io.reactivex.Single
import javax.inject.Inject

class DeleteMoviesWorker(appContext: Context, params: WorkerParameters) :
    RxWorker(appContext, params) {

    @Inject
    lateinit var repository: DatabaseManager

    override fun createWork(): Single<Result> {
        return repository.getAllMovies()
            .doOnSuccess { localMovies ->
                Log.d(WORKER_NAME, "Number of movies in database is ${localMovies.size}")
                for (movie in localMovies) {
                    if (!movie.isFavorite && !movie.isInWatchLater
                        && isMovieLastUpdated2DaysAgo(movie.lastTimeUpdated)) {
                        repository.delete(movie)
                        Log.d(WORKER_NAME, "Deleted movie is ${movie.title}, id is ${movie.id}")
                    }
                }
            }
            .map { Result.success() }
            .onErrorReturn { Result.failure() }
    }

    companion object {
        const val WORKER_NAME = "DeleteMoviesWorker"
    }

}