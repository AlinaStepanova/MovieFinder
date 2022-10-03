package com.avs.moviefinder.ui.recycler_view.adaptes

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.avs.moviefinder.data.dto.Crew
import com.avs.moviefinder.ui.recycler_view.CrewDiffCallback
import com.avs.moviefinder.ui.recycler_view.view_holders.CrewViewHolder

class CrewAdapter :
    ListAdapter<Crew, CrewViewHolder>(CrewDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrewViewHolder {
        return CrewViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CrewViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}