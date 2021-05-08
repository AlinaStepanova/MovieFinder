package com.avs.moviefinder.repository

import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.dto.MoviesFilterResult
import com.avs.moviefinder.data.network.ServerApi
import com.avs.moviefinder.ui.home.MoviesCategory
import com.avs.moviefinder.utils.RxBus
import com.avs.moviefinder.utils.buildNowPlayingUrl
import com.avs.moviefinder.utils.buildPopularMoviesUrl
import com.avs.moviefinder.utils.buildTopRatedMoviesUrl
import java.util.*
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val serverApi: ServerApi,
    private val rxBus: RxBus,
) : BaseRepository() {

    private var moviesDB = ArrayList<Movie>()

    private fun insertMovies(movies: List<Movie>) {
        compositeDisposable.add(databaseManager.insertMovies(movies.filter { it.id > 0 }))
    }

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
                fetchedMovies.forEach { fetchedMovie ->
                    val localMovie = localMovies.firstOrNull { it.id == fetchedMovie.id }
                    if (localMovie != null) {
                        fetchedMovie.isInWatchLater = localMovie.isInWatchLater
                        fetchedMovie.isFavorite = localMovie.isFavorite
                    } else if (fetchedMovie.id != 0L) {
                        insertMovie(fetchedMovie)
                    }
                }
            }
        }
        rxBus.send(MoviesFilterResult(fetchedMovies))
    }

    fun getAllMovies(category: MoviesCategory?) {
        compositeDisposable.add(
            getAllMovies().subscribe({ localMovies ->
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
}