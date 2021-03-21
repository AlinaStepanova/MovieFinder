package com.avs.moviefinder.repository

import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.dto.MoviesSearchFilter
import com.avs.moviefinder.data.network.ServerApi
import com.avs.moviefinder.utils.RxBus
import com.avs.moviefinder.utils.buildMovieByNameUrl
import java.util.*
import javax.inject.Inject

class SearchResultRepository @Inject constructor(
    private val serverApi: ServerApi,
    private val rxBus: RxBus
): BaseRepository() {

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
            databaseManager.getAllMovies().subscribe({ dbMovies ->
                compositeDisposable.add(
                    serverApi.getMovieByTitle(buildMovieByNameUrl(query))
                        .subscribe({ searchedMovies ->
                            combineServerAndDatabaseData(
                                searchedMovies.movies,
                                dbMovies
                            )
                        }, { error -> rxBus.send(error) })
                )
            }, { error -> rxBus.send(error) })
        )
    }

    override fun insertMovie(movie: Movie) {
        compositeDisposable.add(databaseManager.insertMovie(movie))
        if (!movie.isInWatchLater && !movie.isFavorite) {
            deleteMovie(movie)
        }
    }
}