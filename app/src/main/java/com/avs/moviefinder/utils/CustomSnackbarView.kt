package com.avs.moviefinder.utils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.avs.moviefinder.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.ContentViewCallback

class ConnectivitySnackbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), ContentViewCallback {

    var message: TextView

    init {
        View.inflate(context, R.layout.connectivity_snackbar, this)
        message = findViewById(R.id.message)
    }

    override fun animateContentIn(delay: Int, duration: Int) {
        AlphaAnimation(0F, 1F).apply {
            interpolator = DecelerateInterpolator()
            setDuration(ANIMATION_DURATION_MILLIS)
        }.start()
    }

    override fun animateContentOut(delay: Int, duration: Int) {
        AlphaAnimation(1F, 0F).apply {
            interpolator = AccelerateInterpolator()
            setDuration(ANIMATION_DURATION_MILLIS)
        }.start()
    }

    companion object {
        const val ANIMATION_DURATION_MILLIS = 500L
    }
}

class ConnectivitySnackbar(
    parent: ViewGroup,
    content: ConnectivitySnackbarView
) : BaseTransientBottomBar<ConnectivitySnackbar>(parent, content, content) {

    companion object {
        fun make(
            viewGroup: ViewGroup,
            message: String,
            duration: Int,
            anchor: BottomNavigationView,
            backgroundColor: Int
        ): ConnectivitySnackbar {
            val customView = LayoutInflater.from(viewGroup.context).inflate(
                R.layout.layout_connectivity_snackbar,
                viewGroup,
                false
            ) as ConnectivitySnackbarView

            customView.message.text = message

            val snackbar = ConnectivitySnackbar(viewGroup, customView)
            snackbar.view.setBackgroundColor(backgroundColor)

            return snackbar
                .setDuration(duration)
                .setAnchorView(anchor)
        }
    }
}