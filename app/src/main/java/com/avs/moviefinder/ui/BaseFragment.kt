package com.avs.moviefinder.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.content.ContextCompat
import com.avs.moviefinder.R
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.ui.main.MainActivity
import com.avs.moviefinder.ui.movie.MovieActivity
import com.avs.moviefinder.utils.ConnectivitySnackbar
import com.avs.moviefinder.utils.getShareIntent
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment

val MOVIE_EXTRA_TAG: String = Movie::class.java.simpleName

open class BaseFragment : DaggerFragment() {

    protected lateinit var fragmentContext: Context
    private var actionSnackbar: Snackbar? = null
    private var messageSnackbar: Snackbar? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

    override fun onResume() {
        actionSnackbar?.dismiss()
        messageSnackbar?.dismiss()
        super.onResume()
    }

    override fun onPause() {
        actionSnackbar?.dismiss()
        messageSnackbar?.dismiss()
        super.onPause()
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
        messageSnackbar?.dismiss()
        messageSnackbar = Snackbar.make(
            activity.binding.navHostFragment, message,
            Snackbar.LENGTH_LONG
        )
        messageSnackbar?.let { snackbar ->
            snackbar.setBackgroundTint(Color.WHITE)
            snackbar.setTextColor(Color.BLACK)
            snackbar.anchorView = activity.binding.navView
            snackbar.show()
        }
    }

    fun showSnackBarWithAction(message: String, call: () -> Unit) {
        val activity = (activity as MainActivity)
        actionSnackbar?.dismiss()
        actionSnackbar = Snackbar.make(
            activity.binding.navHostFragment, message,
            Snackbar.LENGTH_LONG
        )
        actionSnackbar?.let { snackbar ->
            snackbar.setBackgroundTint(Color.WHITE)
            snackbar.setTextColor(Color.BLACK)
            snackbar.setAction(R.string.snack_bar_action_name) { call.invoke() }
            context?.let {
                snackbar.setActionTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorAccent
                    )
                )
            }
            snackbar.anchorView = activity.binding.navView
            snackbar.show()
        }
    }

    fun showConnectivitySnackBar(message: String) {
        val activity = (activity as MainActivity)
        val color: Int = getConnectivitySnackBarColor()
        ConnectivitySnackbar.make(
            activity.binding.container,
            message,
            Snackbar.LENGTH_LONG,
            activity.binding.navView,
            color
        ).show()
    }

    private fun getConnectivitySnackBarColor(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resources.getColor(R.color.green, fragmentContext.theme)
        } else {
            resources.getColor(R.color.green)
        }
    }

}