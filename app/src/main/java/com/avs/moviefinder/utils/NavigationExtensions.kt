package com.avs.moviefinder.utils

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.avs.moviefinder.R
import com.avs.moviefinder.ui.search_result.SEARCH_RESULT_FRAGMENT_TAG
import com.avs.moviefinder.ui.search_result.SearchResultFragment

fun popFindDetailFragment(fragmentManager: FragmentManager) {
    val position = fragmentManager.backStackEntryCount - 1
    if (position >= 0 && isSameTags(fragmentManager, position)) {
        fragmentManager.popBackStack()
    }
}

private fun isSameTags(fragmentManager: FragmentManager, position: Int) =
    (fragmentManager.getBackStackEntryAt(position).name == SEARCH_RESULT_FRAGMENT_TAG)

fun openFindDetailFragment(fragmentManager: FragmentManager, query: String) {
    val position = fragmentManager.backStackEntryCount - 1
    if ((position == 0 && !isSameTags(fragmentManager, position)) || position == -1) {
        val bundle = Bundle()
        bundle.putString(SearchResultFragment::class.java.simpleName, query)
        val fragment = SearchResultFragment()
        fragment.arguments = bundle
        fragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .addToBackStack(SEARCH_RESULT_FRAGMENT_TAG)
            .commit()
    }
}
