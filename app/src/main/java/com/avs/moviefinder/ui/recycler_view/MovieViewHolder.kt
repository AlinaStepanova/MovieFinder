package com.avs.moviefinder.ui.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.avs.moviefinder.databinding.ItemMovieBinding
import com.avs.moviefinder.data.dto.Movie

class MovieViewHolder private constructor(private val binding: ItemMovieBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        movieClickListener: MovieListener,
        item: Movie
    ) {
        binding.movieClickListener = movieClickListener
        binding.tvMovieTitle.text = item.title
        binding.tvMovieDescription.text = item.overview
        binding.movie = item
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): MovieViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemMovieBinding.inflate(layoutInflater, parent, false)
            return MovieViewHolder(binding)
        }
    }
}