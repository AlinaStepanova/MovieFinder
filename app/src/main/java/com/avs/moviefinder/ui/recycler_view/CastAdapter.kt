package com.avs.moviefinder.ui.recycler_view

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.avs.moviefinder.data.dto.Cast

class CastAdapter(private val listener: CastListener) :
    ListAdapter<Cast, CastViewHolder>(CastDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        return CastViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(listener, item)
    }
}