package com.avs.moviefinder.utils

import android.os.Build
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.avs.moviefinder.R
import com.avs.moviefinder.network.dto.Movie
import com.avs.moviefinder.ui.find.MoviesCategory
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
        background = ContextCompat.getDrawable(context, R.drawable.rounded_button_shape_active)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setTextColor(resources.getColor(R.color.colorAccent, context.theme))
        } else {
            setTextColor(resources.getColor(R.color.colorAccent))
        }
    } else {
        background = ContextCompat.getDrawable(context, R.drawable.rounded_button_shape_inactive)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setTextColor(resources.getColor(R.color.greyDark, context.theme))
        } else {
            setTextColor(resources.getColor(R.color.greyDark))
        }
    }
}

@BindingAdapter("topRatedCategory")
fun TextView.setTopRatedCategoryAppearance(selectedCategory: MoviesCategory) {
    if (selectedCategory == MoviesCategory.TOP_RATED) {
        background = ContextCompat.getDrawable(context, R.drawable.rounded_button_shape_active)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setTextColor(resources.getColor(R.color.colorAccent, context.theme))
        } else {
            setTextColor(resources.getColor(R.color.colorAccent))
        }
    } else {
        background = ContextCompat.getDrawable(context, R.drawable.rounded_button_shape_inactive)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setTextColor(resources.getColor(R.color.greyDark, context.theme))
        } else {
            setTextColor(resources.getColor(R.color.greyDark))
        }
    }
}