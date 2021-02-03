package com.avs.moviefinder.repository

import com.avs.moviefinder.data.database.DatabaseManager
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.network.ServerApi
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val serverApi: ServerApi,
    private val databaseManager: DatabaseManager,
) {
    private val compositeDisposable = CompositeDisposable()

    private fun insertMovies(movies: List<Movie>): Disposable {
        return databaseManager.insertMovies(movies.filter { it.id > 0 })
    }

    private fun deleteMovie(movie: Movie): Disposable {
        return databaseManager.delete(movie)
    }

    fun getAllMovies(): Disposable {
        return databaseManager.getAllMovies()
    }

    fun insertMovie(movie: Movie): Disposable {
        return databaseManager.insertMovie(movie)
    }

    fun updateMovie(movie: Movie): Disposable {
        return databaseManager.update(movie)
    }

    fun getPopularMovies(): Disposable {
        return serverApi.getPopularMovies()
    }

    fun getTopRatedMovies(): Disposable {
        return serverApi.getTopRatedMovies()
    }

    fun getNowPlayingMovies(url: String): Disposable {
        return serverApi.getNowPlayingMovies(url)
    }

    fun combineServerAndDatabaseData(
        moviesDB: ArrayList<Movie>?,
        fetchedMovies: LinkedList<Movie>
    ) {
        moviesDB?.let { localMovies ->
            if (localMovies.isEmpty()) {
                compositeDisposable.add(insertMovies(fetchedMovies))
            } else {
                localMovies.forEach { movie ->
                    val isInFetchedList = fetchedMovies.contains(movie)
                    if (!movie.isInWatchLater && !movie.isFavorite && !isInFetchedList) {
                        compositeDisposable.add(deleteMovie(movie))
                    }
                }
                fetchedMovies.forEach { movie ->
                    val insertedMovie = localMovies.firstOrNull { it.id == movie.id }
                    if (insertedMovie != null) {
                        movie.isInWatchLater = insertedMovie.isInWatchLater
                        movie.isFavorite = insertedMovie.isFavorite
                    } else if (movie.id != 0L) {
                        compositeDisposable.add(insertMovie(movie))
                    }
                }
            }
        }
    }

    fun dispose() {
        compositeDisposable.dispose()
    }
}