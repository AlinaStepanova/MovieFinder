package com.avs.moviefinder.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import com.avs.moviefinder.R
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.ui.main.MainActivity
import com.avs.moviefinder.ui.movie.MovieActivity
import com.avs.moviefinder.utils.getShareIntent
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment

val MOVIE_EXTRA_TAG = Movie::class.java.simpleName

open class BaseFragment : DaggerFragment() {

    protected lateinit var fragmentContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

    fun startMovieActivity(movieId: Long) {
        startActivity(Intent(activity, MovieActivity::class.java).apply {
            putExtra(MOVIE_EXTRA_TAG, movieId)
        })
    }

    fun shareMovie(movieLink: String) {
        startActivity(
            Intent.createChooser(
                getShareIntent(fragmentContext, movieLink),
                resources.getString(R.string.share_via_text)
            )
        )
    }

    fun showSnackBar(message: String) {
        val activity = (activity as MainActivity)
        val snackBar = Snackbar.make(
            activity.binding.navHostFragment, message,
            Snackbar.LENGTH_LONG
        )
        snackBar.setBackgroundTint(Color.WHITE)
        snackBar.setTextColor(Color.BLACK)
        snackBar.anchorView = activity.binding.navView
        snackBar.show()
    }
}