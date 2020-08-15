package com.avs.moviefinder.ui.recycler_view

import androidx.recyclerview.widget.DiffUtil
import com.avs.moviefinder.network.dto.BaseMovie

class MovieDiffCallback : DiffUtil.ItemCallback<BaseMovie>() {
    override fun areItemsTheSame(oldItem: BaseMovie, newItem: BaseMovie): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: BaseMovie, newItem: BaseMovie): Boolean {
        return oldItem == newItem
    }
}