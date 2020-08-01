package com.avs.moviefinder.utils

import android.os.Build
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.avs.moviefinder.R
import com.avs.moviefinder.network.dto.Movie
import com.avs.moviefinder.ui.find.MoviesCategory
import com.google.android.material.imageview.ExperimentalImageView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropTransformation
import jp.wasabeef.picasso.transformations.CropTransformation.GravityHorizontal

@BindingAdapter("releaseDateFormatted")
fun TextView.setReleaseDateFormatted(item: Movie?) {
    item?.let {
        text = formatDate(item.year)
    }
}

@BindingAdapter("ratingFormatted")
fun TextView.setRatingFormatted(item: Movie?) {
    item?.let {
        text = formatRating(item.rating)
    }
}

@BindingAdapter("posterImage")
@ExperimentalImageView
fun ShapeableImageView.setPosterImage(item: Movie) {
    this.shapeAppearanceModel = this.shapeAppearanceModel
        .toBuilder()
        .setTopRightCorner(CornerFamily.ROUNDED, dpToPx(16))
        .setTopLeftCorner(CornerFamily.ROUNDED, dpToPx(16))
        .build()
    Picasso.get()
        .load(POSTER_URL + item.posterPath)
        .transform(
            CropTransformation(
                1F,
                0.5F,
                GravityHorizontal.CENTER,
                CropTransformation.GravityVertical.TOP
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