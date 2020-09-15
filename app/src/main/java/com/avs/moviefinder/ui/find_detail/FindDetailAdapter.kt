package com.avs.moviefinder.ui.find_detail

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.ui.recycler_view.MovieDiffCallback
import com.avs.moviefinder.ui.recycler_view.MovieListener
import com.avs.moviefinder.ui.recycler_view.MovieViewHolder

class FindDetailAdapter(private val movieClickListener: MovieListener) :
    ListAdapter<Movie, MovieViewHolder>(MovieDiffCallback()) {

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(movieClickListener, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder.from(parent)
    }

}