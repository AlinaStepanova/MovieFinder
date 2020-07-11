package com.avs.moviefinder.utils

import androidx.recyclerview.widget.RecyclerView


abstract class RecyclerScroll : RecyclerView.OnScrollListener() {
    var scrollDist = 0
    private var isVisible = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (isVisible && scrollDist > HIDE_THRESHOLD) {
            hide()
            scrollDist = 0
            isVisible = false
        } else if (!isVisible && scrollDist < -SHOW_THRESHOLD) {
            show()
            scrollDist = 0
            isVisible = true
        }

        if (isVisible && dy > 0 || !isVisible && dy < 0) {
            scrollDist += dy
        }
    }

    abstract fun show()
    abstract fun hide()

    companion object {
        private const val HIDE_THRESHOLD = 100f
        private const val SHOW_THRESHOLD = 50f
    }

    
}