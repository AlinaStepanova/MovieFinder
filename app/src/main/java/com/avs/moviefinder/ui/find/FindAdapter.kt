package com.avs.moviefinder.ui.find

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.avs.moviefinder.databinding.ItemMovieBinding
import com.avs.moviefinder.network.dto.Movie


class FindAdapter(private val movieClickListener: MovieListener) :
    ListAdapter<Movie, RecyclerView.ViewHolder>(MovieDiffCallback()) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (holder.itemViewType == 0) {
            (holder as ViewHolder).apply {
                bind(movieClickListener, item)
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

    class ViewHolder private constructor(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            movieClickListener: MovieListener,
            item: Movie
        ) {
            binding.movieClickListener = movieClickListener
            binding.tvMovieTitle.text = "Test"
            binding.tvMovieDescription.text = item.overview
            binding.movie = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemMovieBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem == newItem
    }
}

class MovieListener(
    val movieClickListener: (sleepId: Long) -> Unit,
    val shareListener: (sleepId: Long) -> Unit,
    val watchedClick: (sleepId: Long) -> Unit,
    val watchLaterClick: (sleepId: Long) -> Unit
) {
    fun onClick(movie: Movie) = movieClickListener(movie.id)
    fun onShareClick(movie: Movie) = shareListener(movie.id)
    fun onWatchedClick(movie: Movie) = watchedClick(movie.id)
    fun onWatchLaterClick(movie: Movie) = watchLaterClick(movie.id)
}