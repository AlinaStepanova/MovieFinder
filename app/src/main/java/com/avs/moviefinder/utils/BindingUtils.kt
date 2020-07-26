package com.avs.moviefinder.utils

import android.os.Build
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.avs.moviefinder.R
import com.avs.moviefinder.network.dto.Movie
import com.avs.moviefinder.ui.home.MoviesCategory
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropTransformation

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
fun ImageView.setPosterImage(item: Movie) {
    Picasso.get()
        .load(POSTER_URL + item.posterPath)
        .transform(CropTransformation(0, 0, POSTER_WIDTH, POSTER_WIDTH))
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