package com.avs.moviefinder.ui.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.avs.moviefinder.data.dto.Cast
import com.avs.moviefinder.databinding.ItemCastBinding

class CastViewHolder private constructor(private val binding: ItemCastBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        clickListener: CastListener,
        cast: Cast
    ) {
        binding.clickListener = clickListener
        binding.cast = cast
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): CastViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemCastBinding.inflate(layoutInflater, parent, false)
            return CastViewHolder(binding)
        }
    }
}