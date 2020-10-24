package com.avs.moviefinder.ui.movie

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.avs.moviefinder.R
import com.avs.moviefinder.databinding.ActivityMovieBinding
import com.avs.moviefinder.di.ViewModelFactory
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.ui.MOVIE_EXTRA_TAG
import com.avs.moviefinder.utils.*
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import jp.wasabeef.picasso.transformations.CropTransformation
import javax.inject.Inject

class MovieActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var movieViewModel: MovieViewModel

    lateinit var binding: ActivityMovieBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie)
        movieViewModel = ViewModelProvider(this, viewModelFactory).get(MovieViewModel::class.java)
        setSupportActionBar(binding.toolbar)
        binding.mainViewModel = movieViewModel
        binding.lifecycleOwner = this
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        movieViewModel.openMovieDetails(intent.extras?.getParcelable(MOVIE_EXTRA_TAG))
        movieViewModel.movie.observe(this, {
            it?.let {
                binding.toolbar.title = it.title
                setTagline(it)
                binding.tvOverview.text = it.overview
                binding.tvMovieYear.text = it.releaseDate?.let { it1 -> formatDate(it1, pattern = "dd/MM/yyyy") }
                binding.tvMovieRating.text = it.rating?.let { it1 -> formatRating(it1) }
                binding.tvGenres.text = it.genres?.let { it1 -> formatGenres(it1) }
                binding.tvRuntime.text = formatRuntime(it.runtime)
                binding.fabFavorite.setImageResource(if (it.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border)
                binding.fabWatched.setImageResource(if (it.isInWatchLater) R.drawable.ic_watch_later else R.drawable.ic_outline_watch_later)
                it.posterPath?.let { it1 -> loadImage(it1) }
            }
        })
        movieViewModel.shareBody.observe(this, {
            if (!it.isNullOrEmpty()) shareMovie(it)
        })
        binding.fabFavorite.setOnClickListener { movieViewModel.addToFavorites() }
        binding.fabWatched.setOnClickListener { movieViewModel.addToWatchLater() }
    }

    private fun setTagline(it: Movie) {
        if (it.tagline.isNullOrEmpty()) {
            binding.tvTagline.visibility = View.GONE
        } else {
            binding.tvTagline.text = it.tagline
        }
    }

    private fun loadImage(posterPath: String) {
        Picasso.get()
            .load(POSTER_URL + posterPath)
            .transform(
                CropTransformation(
                    0.95F,
                    CropTransformation.GravityHorizontal.CENTER,
                    CropTransformation.GravityVertical.TOP
                )
            )
            .placeholder(R.drawable.ic_local_movies_grey)
            .error(R.drawable.ic_local_movies_grey)
            .into(binding.ivPoster)
    }

    private fun shareMovie(movieLink: String) {
        startActivity(
            Intent.createChooser(
                getShareIntent(this, movieLink),
                resources.getString(R.string.share_via_text)
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_movie, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_share -> {
                movieViewModel.shareMovie()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}