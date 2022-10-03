package com.avs.moviefinder.ui.recycler_view.adaptes

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.ui.recycler_view.MovieDiffCallback
import com.avs.moviefinder.ui.recycler_view.MovieListener
import com.avs.moviefinder.ui.recycler_view.view_holders.MovieViewHolder

class MoviesPagingAdapter(private val movieClickListener: MovieListener) :
    PagingDataAdapter<Movie, MovieViewHolder>(MovieDiffCallback()) {

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(movieClickListener, item)
        }
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            if (payloads[0] == true) {
                getItem(position)?.let { holder.bindState(it) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder.from(parent)
    }

}