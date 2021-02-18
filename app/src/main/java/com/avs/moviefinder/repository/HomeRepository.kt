package com.avs.moviefinder.repository

import com.avs.moviefinder.data.database.DatabaseManager
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.dto.MoviesFilterResult
import com.avs.moviefinder.data.network.ServerApi
import com.avs.moviefinder.ui.home.MoviesCategory
import com.avs.moviefinder.utils.RxBus
import com.avs.moviefinder.utils.buildNowPlayingUrl
import com.avs.moviefinder.utils.buildPopularMoviesUrl
import com.avs.moviefinder.utils.buildTopRatedMoviesUrl
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val serverApi: ServerApi,
    private val databaseManager: DatabaseManager,
    private val rxBus: RxBus
) {
    private var moviesDB = ArrayList<Movie>()
    private val compositeDisposable = CompositeDisposable()

    private fun insertMovies(movies: List<Movie>) {
        compositeDisposable.add(databaseManager.insertMovies(movies.filter { it.id > 0 }))
    }

    private fun deleteMovie(movie: Movie) = compositeDisposable.add(databaseManager.delete(movie))

    private fun getPopularMovies() {
        val url = buildPopularMoviesUrl()
        compositeDisposable.add(
            serverApi.getPopularMoviesAsSingle(url).subscribe({ fetchedMovies ->
                combineServerAndDatabaseData(
                    moviesDB,
                    fetchedMovies.movies
                )
            }, { error -> rxBus.send(error) })
        )
    }

    private fun getTopRatedMovies() {
        val url = buildTopRatedMoviesUrl()
        compositeDisposable.add(
            serverApi.getTopRatedMovies(url).subscribe({ fetchedMovies ->
                combineServerAndDatabaseData(
                    moviesDB,
                    fetchedMovies.movies
                )
            }, { error -> rxBus.send(error) })
        )
    }

    private fun getNowPlayingMovies(url: String) {
        compositeDisposable.add(
            serverApi.getNowPlayingMovies(url).subscribe({ fetchedMovies ->
                combineServerAndDatabaseData(
                    moviesDB,
                    fetchedMovies.movies
                )
            }, { error -> rxBus.send(error) })
        )
    }

    private fun combineServerAndDatabaseData(
        moviesDB: ArrayList<Movie>,
        fetchedMovies: LinkedList<Movie>
    ) {
        moviesDB.let { localMovies ->
            if (localMovies.isEmpty()) {
                insertMovies(fetchedMovies)
            } else {
                localMovies.forEach { movie ->
                    val isInFetchedList = fetchedMovies.contains(movie)
                    if (!movie.isInWatchLater && !movie.isFavorite && !isInFetchedList) {
                        deleteMovie(movie)
                    }
                }
                fetchedMovies.forEach { movie ->
                    val insertedMovie = localMovies.firstOrNull { it.id == movie.id }
                    if (insertedMovie != null) {
                        movie.isInWatchLater = insertedMovie.isInWatchLater
                        movie.isFavorite = insertedMovie.isFavorite
                    } else if (movie.id != 0L) {
                        insertMovie(movie)
                    }
                }
            }
        }
        rxBus.send(MoviesFilterResult(fetchedMovies))
    }

    fun getAllMovies(category: MoviesCategory?) {
        compositeDisposable.add(
            databaseManager.getAllMovies().subscribe({ localMovies ->
                moviesDB = localMovies as ArrayList<Movie>
                when (category) {
                    MoviesCategory.POPULAR -> getPopularMovies()
                    MoviesCategory.TOP_RATED -> getTopRatedMovies()
                    MoviesCategory.NOW_PLAYING -> {
                        val url = buildNowPlayingUrl()
                        if (url.isNotEmpty()) getNowPlayingMovies(url)
                    }
                    null -> getPopularMovies()
                }
            }, { error -> rxBus.send(error) })
        )
    }

    fun insertMovie(movie: Movie) = compositeDisposable.add(databaseManager.insertMovie(movie))

    fun updateMovie(movie: Movie) = compositeDisposable.add(databaseManager.update(movie))

    fun clear() = compositeDisposable.clear()

}