package com.avs.moviefinder.utils

import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import kotlin.math.abs


abstract class AppBarStateChangeListener : OnOffsetChangedListener {

    enum class State {
        EXPANDED, COLLAPSED, IDLE
    }

    private var currentState = State.IDLE
    override fun onOffsetChanged(appBarLayout: AppBarLayout, offset: Int) {
        currentState = when {
            offset == 0 -> {
                if (currentState != State.EXPANDED) {
                    onStateChanged(appBarLayout, State.EXPANDED)
                }
                State.EXPANDED
            }
            abs(offset) >= appBarLayout.totalScrollRange -> {
                if (currentState != State.COLLAPSED) {
                    onStateChanged(appBarLayout, State.COLLAPSED)
                }
                State.COLLAPSED
            }
            else -> {
                if (currentState != State.IDLE) {
                    onStateChanged(appBarLayout, State.IDLE)
                }
                State.IDLE
            }
        }
    }

    /**
     * Notifies on state change
     * @param appBarLayout Layout
     * @param state Collapse state
     */
    abstract fun onStateChanged(appBarLayout: AppBarLayout?, state: State?)
}