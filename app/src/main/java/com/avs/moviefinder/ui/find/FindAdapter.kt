package com.avs.moviefinder.ui.find

import android.graphics.BitmapFactory
import android.icu.lang.UCharacter.IndicPositionalCategory.LEFT
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.avs.moviefinder.R
import com.avs.moviefinder.network.dto.Movie
import com.avs.moviefinder.utils.POSTER_URL
import com.avs.moviefinder.utils.POSTER_WIDTH
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropTransformation
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import java.net.URL


class FindAdapter : ListAdapter<Movie, FindAdapter.ViewHolder>(MovieDiffCallback()) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.tvMovieTitle)
        private val year: TextView = itemView.findViewById(R.id.tvMovieYear)
        private val description: TextView = itemView.findViewById(R.id.tvMovieDescription)
        private val poster: ImageView = itemView.findViewById(R.id.ivPoster)
        fun bind(item: Movie) {
            title.text = item.title
            year.text = item.year
            description.text = item.overview
            Picasso.get()
                .load(POSTER_URL + item.posterPath)
                .transform(CropTransformation(0, 0, POSTER_WIDTH, POSTER_WIDTH))
                .placeholder(R.drawable.ic_local_movies_grey)
                .into(poster)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.item_movie, parent, false)

                return ViewHolder(view)
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