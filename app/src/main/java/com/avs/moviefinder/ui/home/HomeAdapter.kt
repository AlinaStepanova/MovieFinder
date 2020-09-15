package com.avs.moviefinder.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.avs.moviefinder.ui.recycler_view.MovieDiffCallback
import com.avs.moviefinder.ui.recycler_view.MovieListener
import com.avs.moviefinder.databinding.ItemHeaderBinding
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.ui.recycler_view.MovieViewHolder


class FindAdapter(
    private val movieClickListener: MovieListener,
    private val categoryClickListener: CategoryClickListener
) :
    ListAdapter<Movie, RecyclerView.ViewHolder>(MovieDiffCallback()) {

    private var selectedCategory = MoviesCategory.POPULAR

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (holder.itemViewType == 0) {
            (holder as ViewHolder).apply {
                bind(categoryClickListener, selectedCategory)
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

    fun setSelectedCategory(selectedCategory: MoviesCategory) {
        this.selectedCategory = selectedCategory
    }

    class ViewHolder private constructor(private val binding: ItemHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            categoryClickListener: CategoryClickListener,
            selectedCategory: MoviesCategory
        ) {
            binding.clickListener = categoryClickListener
            binding.selectedCategory = selectedCategory
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

class CategoryClickListener(
    val popularClickListener: () -> Unit,
    val topRatedClickListener: () -> Unit
) {
    fun popularClick() = popularClickListener()
    fun topRatedClick() = topRatedClickListener()
}