package com.avs.moviefinder.ui.recycler_view

import com.avs.moviefinder.data.dto.Cast
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.dto.Result

class MovieListener(
    val movieClickListener: (movie: Movie) -> Unit,
    val shareListener: (movieId: Long) -> Unit,
    val favoritesClick: (movie: Movie) -> Unit,
    val watchLaterClick: (movie: Movie) -> Unit
) {
    fun onClick(movie: Movie) = movieClickListener(movie)
    fun onShareClick(movie: Movie) = shareListener(movie.id)
    fun onFavoritesClick(movie: Movie) = favoritesClick(movie)
    fun onWatchLaterClick(movie: Movie) = watchLaterClick(movie)
}

class MovieClickListener(
    val clickListener: (movie: Movie) -> Unit
) {
    fun onClick(movie: Movie) = clickListener(movie)
}

class CastListener(
    val clickListener: (cast: Cast) -> Unit
) {
    fun onClick(cast: Cast) = clickListener(cast)
}

class ResultListener(
    val clickListener: (result: Result) -> Unit
) {
    fun onClick(result: Result) = clickListener(result)
}