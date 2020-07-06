package com.avs.moviefinder.ui

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.avs.moviefinder.R
import com.avs.moviefinder.ui.main.MainActivity
import com.avs.moviefinder.utils.getShareIntent
import com.google.android.material.snackbar.Snackbar

open class BaseFragment : Fragment() {

    protected lateinit var fragmentContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

    fun shareMovie(movieLink: String) {
        startActivity(
            Intent.createChooser(
                getShareIntent(fragmentContext, movieLink),
                resources.getString(R.string.share_text)
            )
        )
    }

    fun showSnackBar(message: String) {
        val activity = (activity as MainActivity);
        val snackBar = Snackbar.make(
            activity.binding.navHostFragment, message,
            Snackbar.LENGTH_LONG
        )
        snackBar.anchorView = activity.binding.navView
        snackBar.show()
    }
}