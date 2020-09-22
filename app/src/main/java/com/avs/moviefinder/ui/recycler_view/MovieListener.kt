package com.avs.moviefinder.ui.recycler_view

import com.avs.moviefinder.data.dto.Movie

class MovieListener(
    val movieClickListener: (movie: Movie) -> Unit,
    val shareListener: (movieId: Long) -> Unit,
    val watchedClick: (movieId: Long) -> Unit,
    val watchLaterClick: (movieId: Long) -> Unit
) {
    fun onClick(movie: Movie) = movieClickListener(movie)
    fun onShareClick(movie: Movie) = shareListener(movie.id)
    fun onFavoritesClick(movie: Movie) = watchedClick(movie.id)
    fun onWatchLaterClick(movie: Movie) = watchLaterClick(movie.id)
}