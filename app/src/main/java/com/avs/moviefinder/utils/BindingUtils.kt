package com.avs.moviefinder.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.avs.moviefinder.R
import com.avs.moviefinder.network.dto.Movie
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropTransformation

@BindingAdapter("releaseDateFormatted")
fun TextView.setReleaseDateFormatted(item: Movie?) {
    item?.let {
        text = formatDate(item.year)
    }
}

@BindingAdapter("posterImage")
fun ImageView.setPosterImage(item: Movie) {
    Picasso.get()
        .load(POSTER_URL + item.posterPath)
        .transform(CropTransformation(0, 0, POSTER_WIDTH, POSTER_WIDTH))
        .placeholder(R.drawable.ic_local_movies_grey)
        .error(R.drawable.ic_cloud_off)
        .into(this)
}