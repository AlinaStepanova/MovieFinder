package com.avs.moviefinder.utils

import android.content.res.Configuration
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.avs.moviefinder.R
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.ui.home.MoviesCategory
import com.google.android.material.imageview.ExperimentalImageView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropTransformation
import jp.wasabeef.picasso.transformations.CropTransformation.GravityHorizontal
import jp.wasabeef.picasso.transformations.CropTransformation.GravityVertical

@BindingAdapter("releaseDateFormatted")
fun TextView.setReleaseDateFormatted(item: Movie?) {
    item?.let {
        text = item.releaseDate?.let { date -> formatDate(date) }
    }
}

@BindingAdapter("ratingFormatted")
fun TextView.setRatingFormatted(item: Movie?) {
    item?.let {
        val rating = formatRating(it.rating ?: "0")
        if (rating == "0") {
            visibility = View.GONE
        } else {
            text = rating
        }
    }
}

@BindingAdapter("favoritesIcon")
fun ImageView.setFavoritesAppearance(item: Movie?) {
    item?.let {
        val imageResource =
            if (item.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        setImageResource(imageResource)
    }
}

@BindingAdapter("ratingFormattedIcon")
fun ImageView.setRatingFormatted(item: Movie?) {
    item?.let {
        val rating = it.rating?.let { rating -> formatRating(rating) }
        if (rating == "0") {
            visibility = View.GONE
        }
    }
}

@BindingAdapter("watchLaterIcon")
fun ImageView.setWatchLaterAppearance(item: Movie?) {
    item?.let {
        val imageResource =
            if (item.isInWatchLater) R.drawable.ic_watch_later else R.drawable.ic_outline_watch_later
        setImageResource(imageResource)
    }
}

@BindingAdapter("posterImage")
@ExperimentalImageView
fun ShapeableImageView.setPosterImage(item: Movie) {
    val widthRatio = 1F
    val pixels = dpToPx(16)
    var heightRatio = 0.65F
    if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        heightRatio = 0.725F
        this.shapeAppearanceModel = this.shapeAppearanceModel
            .toBuilder()
            .setBottomLeftCorner(CornerFamily.ROUNDED, pixels)
            .setTopLeftCorner(CornerFamily.ROUNDED, pixels)
            .build()
    } else {
        this.shapeAppearanceModel = this.shapeAppearanceModel
            .toBuilder()
            .setTopRightCorner(CornerFamily.ROUNDED, pixels)
            .setTopLeftCorner(CornerFamily.ROUNDED, pixels)
            .build()
    }
    Picasso.get()
        .load(POSTER_URL + item.posterPath)
        .transform(
            CropTransformation(
                widthRatio,
                heightRatio,
                GravityHorizontal.CENTER,
                GravityVertical.TOP
            )
        )
        .placeholder(R.drawable.ic_local_movies_grey)
        .error(R.drawable.ic_local_movies_grey)
        .into(this)
}

@BindingAdapter("popularCategory")
fun TextView.setPopularCategoryAppearance(selectedCategory: MoviesCategory) {
    if (selectedCategory == MoviesCategory.POPULAR) {
        setTextState(R.drawable.rounded_button_shape_active, R.color.colorAccent)
    } else {
        setTextState(R.drawable.rounded_button_shape_inactive, R.color.mainGrey)
    }
}

@BindingAdapter("topRatedCategory")
fun TextView.setTopRatedCategoryAppearance(selectedCategory: MoviesCategory) {
    if (selectedCategory == MoviesCategory.TOP_RATED) {
        setTextState(R.drawable.rounded_button_shape_active, R.color.colorAccent)
    } else {
        setTextState(R.drawable.rounded_button_shape_inactive, R.color.mainGrey)
    }
}

private fun TextView.setTextState(backgroundId: Int, textColorId: Int) {
    background = ContextCompat.getDrawable(context, backgroundId)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        setTextColor(resources.getColor(textColorId, context.theme))
    } else {
        setTextColor(resources.getColor(textColorId))
    }
}