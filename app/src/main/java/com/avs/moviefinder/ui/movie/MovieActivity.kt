package com.avs.moviefinder.ui.movie

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.avs.moviefinder.R
import com.avs.moviefinder.di.ViewModelFactory
import com.avs.moviefinder.network.dto.BaseMovie
import com.avs.moviefinder.network.dto.Movie
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
        movieViewModel.openMovieDetails(intent.extras?.getLong(Movie::class.java.simpleName))
    }
}