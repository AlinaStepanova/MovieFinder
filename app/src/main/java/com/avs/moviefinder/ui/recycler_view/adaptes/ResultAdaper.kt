package com.avs.moviefinder.ui.recycler_view.adaptes

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.avs.moviefinder.data.dto.Result
import com.avs.moviefinder.ui.recycler_view.ResultDiffCallback
import com.avs.moviefinder.ui.recycler_view.ResultListener
import com.avs.moviefinder.ui.recycler_view.view_holders.ResultViewHolder

class ResultAdapter(private val clickListener: ResultListener) :
    ListAdapter<Result, ResultViewHolder>(ResultDiffCallback()) {

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        return ResultViewHolder.from(parent)
    }

}