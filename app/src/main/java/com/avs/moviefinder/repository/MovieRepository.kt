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
        apiMovie?.run {
            if (!imdbId.isNullOrEmpty()) extrasMovie.imdbId = imdbId
            if (!homepage.isNullOrEmpty()) extrasMovie.homepage = homepage
            if (!genres.isNullOrEmpty()) extrasMovie.genres = genres
            if (!tagline.isNullOrEmpty()) extrasMovie.tagline = tagline
            if (!overview.isNullOrEmpty()) extrasMovie.overview = overview
            if (runtime != 0) extrasMovie.runtime = runtime
            if (!title.isNullOrEmpty()) extrasMovie.title = title
            if (!posterPath.isNullOrEmpty()) extrasMovie.posterPath = posterPath
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