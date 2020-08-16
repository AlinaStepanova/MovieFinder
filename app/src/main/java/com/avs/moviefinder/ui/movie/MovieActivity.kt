package com.avs.moviefinder.ui.movie

import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.avs.moviefinder.R
import com.avs.moviefinder.di.ViewModelFactory
import com.avs.moviefinder.ui.MOVIE_EXTRA_TAG
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject


class MovieActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var movieViewModel: MovieViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)
        movieViewModel = ViewModelProvider(this, viewModelFactory).get(MovieViewModel::class.java)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        movieViewModel.openMovieDetails(intent.extras?.getLong(MOVIE_EXTRA_TAG))
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}