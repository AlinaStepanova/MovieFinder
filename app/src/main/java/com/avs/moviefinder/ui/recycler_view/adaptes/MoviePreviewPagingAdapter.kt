package com.avs.moviefinder.ui.recycler_view.adaptes

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.ui.recycler_view.MovieClickListener
import com.avs.moviefinder.ui.recycler_view.MovieDiffCallback
import com.avs.moviefinder.ui.recycler_view.view_holders.MoviePreviewViewHolder

class MoviePreviewPagingAdapter(private val movieClickListener: MovieClickListener) :
    PagingDataAdapter<Movie, MoviePreviewViewHolder>(MovieDiffCallback()) {

    override fun onBindViewHolder(holder: MoviePreviewViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(movieClickListener, item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviePreviewViewHolder {
        return MoviePreviewViewHolder.from(parent)
    }

}