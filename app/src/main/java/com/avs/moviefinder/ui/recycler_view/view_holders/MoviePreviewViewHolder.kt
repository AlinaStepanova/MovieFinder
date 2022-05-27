package com.avs.moviefinder.ui.recycler_view.view_holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.databinding.ItemMoviePreviewBinding
import com.avs.moviefinder.ui.recycler_view.MovieClickListener

class MoviePreviewViewHolder private constructor(private val binding: ItemMoviePreviewBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        movieClickListener: MovieClickListener,
        item: Movie
    ) {
        binding.movieClickListener = movieClickListener
        binding.tvMovieTitle.text = item.title
        binding.movie = item
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): MoviePreviewViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemMoviePreviewBinding.inflate(layoutInflater, parent, false)
            return MoviePreviewViewHolder(binding)
        }
    }
}