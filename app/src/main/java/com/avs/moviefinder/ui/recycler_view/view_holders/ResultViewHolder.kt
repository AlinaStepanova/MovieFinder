package com.avs.moviefinder.ui.recycler_view.view_holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.avs.moviefinder.data.dto.Result
import com.avs.moviefinder.databinding.ItemSimilarBinding
import com.avs.moviefinder.ui.recycler_view.ResultListener

class ResultViewHolder private constructor(private val binding: ItemSimilarBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        clickListener: ResultListener,
        result: Result
    ) {
        binding.clickListener = clickListener
        binding.result = result
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): ResultViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemSimilarBinding.inflate(layoutInflater, parent, false)
            return ResultViewHolder(binding)
        }
    }
}