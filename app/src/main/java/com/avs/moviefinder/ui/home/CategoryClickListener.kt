package com.avs.moviefinder.ui.home

class CategoryClickListener(
    val popularClickListener: () -> Unit,
    val topRatedClickListener: () -> Unit,
    val nowPlayingClickListener: () -> Unit,
    val upcomingClickListener: () -> Unit
) {
    fun popularClick() = popularClickListener()
    fun topRatedClick() = topRatedClickListener()
    fun nowPlayingClick() = nowPlayingClickListener()
    fun upcomingClick() = upcomingClickListener()
}