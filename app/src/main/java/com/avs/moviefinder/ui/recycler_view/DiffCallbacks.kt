package com.avs.moviefinder.ui.recycler_view

import androidx.recyclerview.widget.DiffUtil
import com.avs.moviefinder.data.dto.Cast
import com.avs.moviefinder.data.dto.Crew
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.dto.Result

class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: Movie, newItem: Movie): Any? {
        return if (oldItem.isFavorite != newItem.isFavorite || oldItem.isInWatchLater != newItem.isInWatchLater) true else null
    }
}

class CastDiffCallback : DiffUtil.ItemCallback<Cast>() {
    override fun areItemsTheSame(oldItem: Cast, newItem: Cast): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Cast, newItem: Cast): Boolean {
        return oldItem == newItem
    }
}

class CrewDiffCallback : DiffUtil.ItemCallback<Crew>() {
    override fun areItemsTheSame(oldItem: Crew, newItem: Crew): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Crew, newItem: Crew): Boolean {
        return oldItem == newItem
    }
}

class ResultDiffCallback : DiffUtil.ItemCallback<Result>() {
    override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
        return oldItem == newItem
    }
}