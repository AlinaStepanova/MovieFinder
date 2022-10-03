package com.avs.moviefinder.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.avs.moviefinder.R
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.ui.main.MainActivity
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

    protected fun startMovieActivity(movie: Movie) {
        (activity as MainActivity).navigateToMovieActivity(movie)
    }

    protected fun shareMovie(movieLink: String) {
        startActivity(
            Intent.createChooser(
                getShareIntent(fragmentContext, movieLink),
                resources.getString(R.string.share_via_text)
            )
        )
    }

    protected fun showSnackBar(message: String) {
        val activity = (activity as MainActivity)
        messageSnackbar?.dismiss()
        messageSnackbar = Snackbar.make(
            activity.binding.navHostFragment, message,
            Snackbar.LENGTH_LONG
        )
        messageSnackbar?.let { snackbar ->
            snackbar.setBackgroundTint(Color.WHITE)
            snackbar.setTextColor(Color.BLACK)
            if (activity.binding.bottomNav.isVisible) {
                snackbar.anchorView = activity.binding.bottomNav
            }
            snackbar.show()
        }
    }

    protected fun showSnackBarWithAction(message: String, call: () -> Unit) {
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
            if (activity.binding.bottomNav.isVisible) {
                snackbar.anchorView = activity.binding.bottomNav
            }
            snackbar.show()
        }
    }

    protected fun itemTouchCallback(removeItemFromList: (position: Int) -> Unit) = object :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ) = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) =
            removeItemFromList(viewHolder.absoluteAdapterPosition)
    }
}