package com.avs.moviefinder.repository

import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.network.ServerApi
import com.avs.moviefinder.utils.RxBus
import com.avs.moviefinder.utils.buildMovieByIdUrl
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val serverApi: ServerApi,
    private val rxBus: RxBus
) : BaseRepository() {
    private var extrasMovie = Movie()

    private fun combineTwoMovies(apiMovie: Movie?, dbMovie: Movie?) {
        if (dbMovie != null) {
            extrasMovie = dbMovie
        }
        apiMovie?.let {
            if (!it.imdbId.isNullOrEmpty()) extrasMovie.imdbId = it.imdbId
            if (!it.homepage.isNullOrEmpty()) extrasMovie.homepage = it.homepage
            if (!it.genres.isNullOrEmpty()) extrasMovie.genres = it.genres
            if (!it.tagline.isNullOrEmpty()) extrasMovie.tagline = it.tagline
            if (!it.overview.isNullOrEmpty()) extrasMovie.overview = it.overview
            if (it.runtime != 0) extrasMovie.runtime = it.runtime
            if (!it.title.isNullOrEmpty()) extrasMovie.title = it.title
            if (!it.posterPath.isNullOrEmpty()) extrasMovie.posterPath = it.posterPath
        }
    }

    fun getMovieData(movie: Movie) {
        extrasMovie = movie
        compositeDisposable.add(Single.zip(
            serverApi.getMovieById(buildMovieByIdUrl(movie.id))
                .onErrorReturn { extrasMovie },
            databaseManager.getMovieByIdAsSingle(movie.id)
                .onErrorReturn { extrasMovie }
        ) { apiMovie, dbMovie -> combineTwoMovies(apiMovie, dbMovie) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (extrasMovie.isFavorite || extrasMovie.isInWatchLater) {
                    insertMovie(extrasMovie)
                } else {
                    rxBus.send(extrasMovie)
                }
            }, {})
        )
    }
}