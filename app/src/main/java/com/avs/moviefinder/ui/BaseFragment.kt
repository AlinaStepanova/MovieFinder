package com.avs.moviefinder.ui

import androidx.fragment.app.Fragment
import com.avs.moviefinder.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar

open class BaseFragment : Fragment() {

    fun showSnackBar(message: String) {
        val snackBar = Snackbar.make(
            (activity as MainActivity).binding.navHostFragment, message,
            Snackbar.LENGTH_LONG
        )
        snackBar.show()
    }
}