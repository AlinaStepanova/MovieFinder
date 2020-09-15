package com.avs.moviefinder.ui.recycler_view

import com.avs.moviefinder.data.dto.Movie

class MovieListener(
    val movieClickListener: (sleepId: Long) -> Unit,
    val shareListener: (sleepId: Long) -> Unit,
    val watchedClick: (sleepId: Long) -> Unit,
    val watchLaterClick: (sleepId: Long) -> Unit
) {
    fun onClick(movie: Movie) = movieClickListener(movie.id)
    fun onShareClick(movie: Movie) = shareListener(movie.id)
    fun onFavoritesClick(movie: Movie) = watchedClick(movie.id)
    fun onWatchLaterClick(movie: Movie) = watchLaterClick(movie.id)
}