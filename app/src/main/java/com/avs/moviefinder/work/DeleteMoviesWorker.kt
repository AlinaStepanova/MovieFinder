package com.avs.moviefinder.work

import android.content.Context
import android.util.Log
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.avs.moviefinder.data.database.DatabaseManager
import com.avs.moviefinder.utils.isMovieLastUpdated2DaysAgo
import com.avs.moviefinder.work.factory.ChildWorkerFactory
import io.reactivex.Single
import javax.inject.Inject

class DeleteMoviesWorker(
    private val databaseManager: DatabaseManager,
    appContext: Context,
    params: WorkerParameters
) : RxWorker(appContext, params) {

    override fun createWork(): Single<Result> {
        return databaseManager.getAllMovies()
            .doOnSuccess { localMovies ->
                Log.d(WORKER_NAME, "Number of movies in database is ${localMovies.size}")
                for (movie in localMovies) {
                    if (!movie.isFavorite && !movie.isInWatchLater
                        && isMovieLastUpdated2DaysAgo(movie.lastTimeUpdated)
                    ) {
                        databaseManager.delete(movie)
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

    class Factory @Inject constructor(
        private var databaseManager: DatabaseManager,
    ) : ChildWorkerFactory {

        override fun create(appContext: Context, params: WorkerParameters): RxWorker {
            return DeleteMoviesWorker(databaseManager, appContext, params)
        }
    }

}