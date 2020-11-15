package com.avs.moviefinder.ui.movie

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import com.avs.moviefinder.R
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.databinding.ActivityMovieBinding
import com.avs.moviefinder.di.ViewModelFactory
import com.avs.moviefinder.ui.MOVIE_EXTRA_TAG
import com.avs.moviefinder.utils.*
import com.avs.moviefinder.utils.AppBarStateChangeListener.State.EXPANDED
import com.avs.moviefinder.utils.AppBarStateChangeListener.State.IDLE
import com.google.android.material.appbar.AppBarLayout
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import dagger.android.support.DaggerAppCompatActivity
import jp.wasabeef.picasso.transformations.CropTransformation
import javax.inject.Inject
import kotlin.properties.Delegates

class MovieActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var movieViewModel: MovieViewModel

    lateinit var binding: ActivityMovieBinding
    var statusBarColor by Delegates.notNull<Int>()

    private val target = initTarget()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie)
        movieViewModel = ViewModelProvider(this, viewModelFactory).get(MovieViewModel::class.java)
        setSupportActionBar(binding.toolbar)
        statusBarColor = getPrimaryDarkColor()
        binding.mainViewModel = movieViewModel
        binding.lifecycleOwner = this
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        movieViewModel.openMovieDetails(intent.extras?.getParcelable(MOVIE_EXTRA_TAG))
        binding.ivPoster.tag = target
        binding.appBar.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?) =
                setAppBarColor(state)

        })
        movieViewModel.movie.observe(this, {
            it?.let {
                binding.toolbar.title = it.title
                setTagline(it)
                binding.tvOverview.text = it.overview
                binding.tvMovieYear.text = it.releaseDate?.let { date -> formatDate(date) }
                formatRating(it)
                formatRuntime(it)
                binding.tvGenres.text = it.genres?.let { genres -> formatGenres(genres) }
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

    private fun formatRuntime(it: Movie) {
        val runtime = formatRuntime(it.runtime)
        if (runtime.isEmpty()) {
            binding.ivHourglass.visibility = View.GONE
            binding.tvRuntime.visibility = View.GONE
        } else {
            binding.tvRuntime.text = runtime
        }
    }

    private fun formatRating(it: Movie) {
        val rating = it.rating?.let { rating -> formatRating(rating) }
        if (rating == "0") {
            binding.tvMovieRating.visibility = View.GONE
            binding.ivStar.visibility = View.GONE
        } else {
            binding.tvMovieRating.text = rating
        }
    }

    override fun onDestroy() {
        Picasso.get().cancelRequest(target)
        super.onDestroy()
    }

    override fun onBackPressed() {
        val isMovieUpdated = movieViewModel.isInitialMovieUpdated()
        if (isMovieUpdated) {
            val resultIntent = intent
            resultIntent.putExtra(IS_MOVIE_UPDATED_EXTRA, isMovieUpdated)
            resultIntent.putExtra(MOVIE_EXTRA_TAG, movieViewModel.movie.value)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
        super.onBackPressed()
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
            .into(target)
    }

    private fun setAppBarColor(state: AppBarStateChangeListener.State?) {
        this@MovieActivity.window.statusBarColor = when (state) {
            EXPANDED -> {
                statusBarColor
            }
            IDLE -> {
                ColorUtils.setAlphaComponent(statusBarColor, 180)
            }
            else -> {
                getPrimaryDarkColor()
            }
        }
    }

    private fun initTarget(): Target {
        return object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                bitmap?.let {
                    binding.ivPoster.setImageBitmap(bitmap)
                    Palette.from(bitmap)
                        .generate { palette ->
                            val swatch = palette!!.dominantSwatch
                            swatch?.let {
                                statusBarColor = it.rgb
                                this@MovieActivity.window.statusBarColor = statusBarColor
                            }
                        }
                }
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
        }
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

    private fun getPrimaryDarkColor() =
        ContextCompat.getColor(this, R.color.colorPrimaryDark)
}