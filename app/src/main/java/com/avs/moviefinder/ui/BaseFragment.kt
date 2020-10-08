package com.avs.moviefinder.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
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

    fun startMovieActivity(movie: Movie) {
        startActivity(Intent(activity, MovieActivity::class.java).apply {
            putExtra(MOVIE_EXTRA_TAG, movie)
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

    fun showSnackBarWithAction(message: String, call: () -> Unit) {
        val activity = (activity as MainActivity)
        val snackBar = Snackbar.make(
            activity.binding.navHostFragment, message,
            Snackbar.LENGTH_LONG
        )
        snackBar.setBackgroundTint(Color.WHITE)
        snackBar.setTextColor(Color.BLACK)
        snackBar.setAction(R.string.snack_bar_action_name) { call.invoke() }
        context?.let {
            snackBar.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
        }
        snackBar.anchorView = activity.binding.navView
        snackBar.show()
    }
}