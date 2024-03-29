package com.avs.moviefinder.ui.movie

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navArgs
import androidx.palette.graphics.Palette
import com.avs.moviefinder.R
import com.avs.moviefinder.data.dto.*
import com.avs.moviefinder.databinding.ActivityMovieBinding
import com.avs.moviefinder.di.factories.ViewModelFactory
import com.avs.moviefinder.ui.recycler_view.CastListener
import com.avs.moviefinder.ui.recycler_view.ResultListener
import com.avs.moviefinder.ui.recycler_view.adaptes.CastAdapter
import com.avs.moviefinder.ui.recycler_view.adaptes.CrewAdapter
import com.avs.moviefinder.ui.recycler_view.adaptes.ResultAdapter
import com.avs.moviefinder.utils.*
import com.avs.moviefinder.utils.AppBarStateChangeListener.State.EXPANDED
import com.avs.moviefinder.utils.AppBarStateChangeListener.State.IDLE
import com.google.android.material.appbar.AppBarLayout
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import dagger.android.support.DaggerAppCompatActivity
import jp.wasabeef.picasso.transformations.CropTransformation
import javax.inject.Inject

class MovieActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var movieViewModel: MovieViewModel

    lateinit var binding: ActivityMovieBinding
    var statusBarColor: Int = 0

    private val args: MovieActivityArgs by navArgs()
    private val target = initTarget()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie)
        movieViewModel = ViewModelProvider(this, viewModelFactory)[MovieViewModel::class.java]
        setSupportActionBar(binding.toolbar)
        statusBarColor = getPrimaryDarkColor()
        binding.mainViewModel = movieViewModel
        binding.lifecycleOwner = this
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val extrasMovie: Movie = args.movie
        binding.toolbarLayout.title = extrasMovie.title ?: ""
        loadMovie(extrasMovie)
        val castAdapter = CastAdapter(CastListener { })
        movieViewModel.cast.observe(this, observeCast(castAdapter))
        val crewAdapter = CrewAdapter()
        movieViewModel.crew.observe(this, observeCrew(crewAdapter))
        val similarAdapter = ResultAdapter(ResultListener { result -> loadMovie(result.toMovie()) })
        movieViewModel.similarMovies.observe(this, observeSimilarMovies(similarAdapter))
        binding.rvCast.adapter = castAdapter
        binding.rvCrew.adapter = crewAdapter
        binding.rvSimilar.adapter = similarAdapter
        binding.ivPoster.tag = target
        binding.tvLinks.movementMethod = LinkMovementMethod.getInstance()
        binding.appBar.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?) =
                setAppBarColor(state)

        })
        movieViewModel.movie.observe(this) {
            it?.let {
                stopShimmerAnimation()
                setTagline(it)
                binding.tvOverview.text = it.overview
                formatReleaseDate(it)
                formatRating(it)
                formatRuntime(it)
                formatCountries(it)
                formatGenres(it)
                binding.toolbarLayout.title = it.title
                binding.tvLinks.text = buildLinks(
                    it.imdbId,
                    it.homepage,
                    resources.getString(R.string.homepage)
                )
                binding.fabFavorite.setImageResource(if (it.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border)
                binding.fabWatched.setImageResource(if (it.isInWatchLater) R.drawable.ic_watch_later else R.drawable.ic_outline_watch_later)
            }
        }
        movieViewModel.shareBody.observe(this) {
            if (!it.isNullOrEmpty()) shareMovie(it)
        }
        binding.fabFavorite.setOnClickListener { movieViewModel.addToFavorites() }
        binding.fabWatched.setOnClickListener { movieViewModel.addToWatchLater() }
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

    override fun onStop() {
        stopShimmerAnimation()
        super.onStop()
    }

    override fun onDestroy() {
        Picasso.get().cancelRequest(target)
        super.onDestroy()
    }

    private fun loadMovie(movie: Movie?) {
        movieViewModel.openMovieDetails(movie)
        loadImage(movie?.posterPath ?: "")
        binding.appBar.setExpanded(true, true)
        binding.scrollContainer.smoothScrollTo(0, 0)
    }

    private fun observeSimilarMovies(similarAdapter: ResultAdapter): (list: List<Result>) -> Unit =
        {
            if (it.isEmpty()) {
                binding.rvSimilar.visibility = View.GONE
            } else {
                binding.tvSimilar.visibility = View.VISIBLE
                binding.rvSimilar.visibility = View.VISIBLE
                similarAdapter.submitList(it)
                binding.rvSimilar.smoothScrollToPosition(0)
            }
        }

    private fun observeCast(castAdapter: CastAdapter): (list: List<Cast>) -> Unit =
        {
            if (it.isEmpty()) {
                binding.rvCast.visibility = View.GONE
            } else {
                binding.tvCast.visibility = View.VISIBLE
                binding.rvCast.visibility = View.VISIBLE
                castAdapter.submitList(it)
                binding.rvCast.smoothScrollToPosition(0)
            }
        }

    private fun observeCrew(crewAdapter: CrewAdapter): (list: List<Crew>) -> Unit =
        {
            if (it.isEmpty()) {
                binding.rvCrew.visibility = View.GONE
            } else {
                binding.tvCrew.visibility = View.VISIBLE
                binding.rvCrew.visibility = View.VISIBLE
                crewAdapter.submitList(it)
                binding.rvCrew.smoothScrollToPosition(0)
            }
        }

    private fun stopShimmerAnimation() {
        binding.shimmerViewContainer.visibility = View.GONE
        binding.shimmerViewContainer.stopShimmerAnimation()
    }

    private fun formatReleaseDate(movie: Movie) {
        val runtime = movie.releaseDate?.let { date -> formatDate(date) }
        binding.ivCalendar.visibility = View.VISIBLE
        if (runtime.isNullOrEmpty()) {
            binding.tvMovieYear.text = getString(R.string.unknown_text)
            binding.tvMovieYear.visibility = View.VISIBLE
        } else {
            binding.tvMovieYear.text = runtime
            binding.tvMovieYear.visibility = View.VISIBLE
        }
    }

    private fun formatRuntime(movie: Movie) {
        val runtime = formatRuntime(
            movie.runtime,
            resources.getString(R.string.hours),
            resources.getString(R.string.minutes)
        )
        if (runtime.isNotEmpty()) {
            binding.ivHourglass.visibility = View.VISIBLE
            binding.tvRuntime.visibility = View.VISIBLE
            binding.tvRuntime.text = runtime
        } else {
            binding.ivHourglass.visibility = View.GONE
            binding.tvRuntime.visibility = View.GONE
        }
    }

    private fun formatRating(movie: Movie) {
        val rating = movie.rating?.let { rating -> formatRating(rating) }
        if (rating != "0") {
            binding.tvMovieRating.text = rating
            binding.tvMovieRating.visibility = View.VISIBLE
            binding.ivStar.visibility = View.VISIBLE
        } else {
            binding.tvMovieRating.visibility = View.GONE
            binding.ivStar.visibility = View.GONE
        }
    }

    private fun formatGenres(movie: Movie) {
        val genres = movie.genres?.let { genres -> formatGenres(genres) }
        if (!genres.isNullOrEmpty()) {
            binding.tvGenres.visibility = View.VISIBLE
            binding.tvGenres.text = genres
        } else {
            binding.tvGenres.visibility = View.GONE
        }
    }

    private fun formatCountries(movie: Movie) {
        val countries = movie.countries?.let { it -> formatCountries(it) }
        if (!countries.isNullOrEmpty()) {
            binding.ivLocation.visibility = View.VISIBLE
            binding.tvCountries.visibility = View.VISIBLE
            binding.tvCountries.text = countries
        } else {
            binding.ivLocation.visibility = View.GONE
            binding.tvCountries.visibility = View.GONE
        }
    }

    private fun setTagline(movie: Movie) {
        if (!movie.tagline.isNullOrEmpty()) {
            binding.tvTagline.visibility = View.VISIBLE
            binding.tvTagline.text = movie.tagline
        } else {
            binding.tvTagline.visibility = View.GONE
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
                            val swatch = palette?.dominantSwatch
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

    private fun getPrimaryDarkColor() =
        ContextCompat.getColor(this, R.color.colorPrimaryDark)
}