package com.avs.moviefinder.ui.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.avs.moviefinder.ui.recycler_view.MovieListener
import com.avs.moviefinder.R
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.databinding.FragmentHomeBinding
import com.avs.moviefinder.di.ViewModelFactory
import com.avs.moviefinder.data.network.ErrorType
import com.avs.moviefinder.ui.BaseFragment
import com.avs.moviefinder.ui.MOVIE_EXTRA_TAG
import com.avs.moviefinder.ui.movie.MovieActivity
import javax.inject.Inject


class HomeFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var homeViewModel: HomeViewModel

    private lateinit var binding: FragmentHomeBinding

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let {
                    homeViewModel.handleOnActivityResult(it)
                }
            }
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        homeViewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )
        val root: View = binding.root
        binding.findViewModel = homeViewModel
        binding.lifecycleOwner = this
        val adapter = FindAdapter(
            MovieListener(
                { movie -> startMovieActivityForResult(movie) },
                { movieId -> homeViewModel.shareMovie(movieId) },
                { movieId -> homeViewModel.addToFavorites(movieId) }
            ) { movieId -> homeViewModel.addToWatchLater(movieId) },
            CategoryClickListener(
                { homeViewModel.onPopularClick() },
                { homeViewModel.onTopRatedClick() }
            )
        )
        binding.rvFindRecyclerView.adapter = adapter
        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        homeViewModel.movies.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })
        homeViewModel.updateMovieIndex.observe(viewLifecycleOwner, {
            it?.let {
                adapter.notifyItemChanged(it)
            }
        })
        homeViewModel.selectedCategory.observe(viewLifecycleOwner, {
            it?.let {
                adapter.setSelectedCategory(it)
            }
        })
        homeViewModel.shareBody.observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) shareMovie(it)
        })
        homeViewModel.isProgressVisible.observe(viewLifecycleOwner, {
            binding.pbFindProgress.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })
        homeViewModel.errorType.observe(viewLifecycleOwner, {
            handleErrorEvent(it)
        })
        return root
    }

    private fun startMovieActivityForResult(movie: Movie) {
        resultLauncher.launch(
            Intent(activity, MovieActivity::class.java).apply {
                putExtra(MOVIE_EXTRA_TAG, movie)
            }
        )
    }

    private fun handleErrorEvent(it: ErrorType?) {
        when (it) {
            null -> {
                binding.ivError.visibility = View.INVISIBLE
                binding.tvErrorText.visibility = View.INVISIBLE
            }
            ErrorType.NETWORK -> {
                binding.ivError.visibility = View.VISIBLE
                binding.tvErrorText.visibility = View.INVISIBLE
                showSnackBar(resources.getString(R.string.network_error_occurred))
            }
            ErrorType.NO_RESULTS -> {
                binding.ivError.visibility = View.INVISIBLE
                binding.tvErrorText.visibility = View.VISIBLE
                showSnackBar(resources.getString(R.string.no_results_found))
            }
            else -> {
            }
        }
    }
}