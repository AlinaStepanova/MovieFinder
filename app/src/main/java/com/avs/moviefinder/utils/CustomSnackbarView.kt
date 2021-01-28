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

class IconSnackbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), ContentViewCallback {

    var message: TextView

    init {
        View.inflate(context, R.layout.icon_snackbar, this)
        message = findViewById(R.id.message)
    }

    override fun animateContentIn(delay: Int, duration: Int) {
        AlphaAnimation(0F, 1F).apply {
            interpolator = DecelerateInterpolator()
            setDuration(500)
        }.start()
    }

    override fun animateContentOut(delay: Int, duration: Int) {
        AlphaAnimation(1F, 0F).apply {
            interpolator = AccelerateInterpolator()
            setDuration(500)
        }.start()
    }
}

class IconSnackbar(
    parent: ViewGroup,
    content: IconSnackbarView
) : BaseTransientBottomBar<IconSnackbar>(parent, content, content) {

    companion object {
        fun make(
            viewGroup: ViewGroup,
            message: String,
            duration: Int,
            anchor: BottomNavigationView,
            backgroundColor: Int
        ): IconSnackbar {
            val customView = LayoutInflater.from(viewGroup.context).inflate(
                R.layout.layout_icon_snackbar,
                viewGroup,
                false
            ) as IconSnackbarView

            customView.message.text = message

            val snackbar = IconSnackbar(viewGroup, customView)
            snackbar.view.setBackgroundColor(backgroundColor)

            return snackbar
                .setDuration(duration)
                .setAnchorView(anchor)
        }
    }
}