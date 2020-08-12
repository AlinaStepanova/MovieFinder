package com.avs.moviefinder.ui.movie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.avs.moviefinder.R

class MovieActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }
}