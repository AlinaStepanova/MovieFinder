package com.avs.moviefinder.ui.find

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.avs.moviefinder.ui.recycler_view.MovieDiffCallback
import com.avs.moviefinder.ui.recycler_view.MovieListener
import com.avs.moviefinder.databinding.ItemHeaderBinding
import com.avs.moviefinder.network.dto.Movie
import com.avs.moviefinder.ui.recycler_view.MovieViewHolder


class FindAdapter(private val movieClickListener: MovieListener) :
    ListAdapter<Movie, RecyclerView.ViewHolder>(MovieDiffCallback()) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (holder.itemViewType == 0) {
            (holder as ViewHolder).apply {
                bind()
            }
        } else {
            (holder as MovieViewHolder).apply {
                bind(movieClickListener, item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            ViewHolder.from(parent)
        } else {
            MovieViewHolder.from(parent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class ViewHolder private constructor(private val binding: ItemHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemHeaderBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}