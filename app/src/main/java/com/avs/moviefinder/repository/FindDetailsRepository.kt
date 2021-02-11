package com.avs.moviefinder.repository

import com.avs.moviefinder.data.database.DatabaseManager
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.dto.MoviesSearchFilter
import com.avs.moviefinder.data.network.ServerApi
import com.avs.moviefinder.utils.RxBus
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FindDetailsRepository @Inject constructor(
    private val serverApi: ServerApi,
    private val databaseManager: DatabaseManager,
    private val rxBus: RxBus
) {
    private val compositeDisposable = CompositeDisposable()

    private fun combineServerAndDatabaseData(
        searchedMovies: LinkedList<Movie>,
        dbMovies: List<Movie>
    ) {
        searchedMovies.forEach { movie ->
            val insertedMovie = dbMovies.firstOrNull { it.id == movie.id }
            if (insertedMovie != null) {
                movie.isInWatchLater = insertedMovie.isInWatchLater
                movie.isFavorite = insertedMovie.isFavorite
            }
        }
        rxBus.send(MoviesSearchFilter(searchedMovies))
    }

    fun getSubmittedQuery(query: String) {
        compositeDisposable.add(
            databaseManager.getAllMoviesAsSingle().subscribe({ dbMovies ->
                compositeDisposable.add(
                    serverApi.getMovieByTitleAsSingle(query).subscribe({ searchedMovies ->
                        combineServerAndDatabaseData(
                            searchedMovies.movies,
                            dbMovies
                        )
                    }, { error -> rxBus.send(error) })
                )
            }, { error -> rxBus.send(error) })
        )
    }

    fun deleteMovie(movie: Movie) = compositeDisposable.add(databaseManager.delete(movie))

    fun insertMovie(movie: Movie) = compositeDisposable.add(databaseManager.insertMovie(movie))

    fun updateMovie(movie: Movie) = compositeDisposable.add(databaseManager.update(movie))

    fun clear() = compositeDisposable.clear()
}