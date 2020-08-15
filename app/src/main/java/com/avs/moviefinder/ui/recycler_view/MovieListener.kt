package com.avs.moviefinder.ui.recycler_view

import com.avs.moviefinder.network.dto.BaseMovie

class MovieListener(
    val movieClickListener: (sleepId: Long) -> Unit,
    val shareListener: (sleepId: Long) -> Unit,
    val watchedClick: (sleepId: Long) -> Unit,
    val watchLaterClick: (sleepId: Long) -> Unit
) {
    fun onClick(baseMovie: BaseMovie) = movieClickListener(baseMovie.id)
    fun onShareClick(baseMovie: BaseMovie) = shareListener(baseMovie.id)
    fun onWatchedClick(baseMovie: BaseMovie) = watchedClick(baseMovie.id)
    fun onWatchLaterClick(baseMovie: BaseMovie) = watchLaterClick(baseMovie.id)
}