package com.avs.moviefinder.ui.recycler_view.view_holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.avs.moviefinder.data.dto.Crew
import com.avs.moviefinder.databinding.ItemCrewBinding

class CrewViewHolder private constructor(private val binding: ItemCrewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        crew: Crew
    ) {
        binding.crew = crew
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): CrewViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemCrewBinding.inflate(layoutInflater, parent, false)
            return CrewViewHolder(binding)
        }
    }
}