package com.avs.moviefinder.ui.movie

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.avs.moviefinder.R
import com.avs.moviefinder.databinding.ActivityMovieBinding
import com.avs.moviefinder.di.ViewModelFactory
import com.avs.moviefinder.network.dto.Movie
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
        movieViewModel.openMovieDetails(intent.extras?.getLong(MOVIE_EXTRA_TAG))
        movieViewModel.movie.observe(this, {
            it?.let {
                binding.toolbar.title = it.title
                setTagline(it)
                binding.tvOverview.text = it.overview
                binding.tvMovieYear.text = formatDate(it.releaseDate, pattern = "dd/MM/yyyy")
                binding.tvMovieRating.text = formatRating(it.rating)
                binding.tvGenres.text = formatGenres(it.genres)
                binding.tvRuntime.text = formatRuntime(it.runtime)
                loadImage(it.posterPath)
            }
        })
        movieViewModel.shareBody.observe(this, {
            if (!it.isNullOrEmpty()) shareMovie(it)
        })
    }

    private fun setTagline(it: Movie) {
        if (it.tagline.isEmpty()) {
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