package com.avs.moviefinder.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.avs.moviefinder.ui.recycler_view.MovieListener
import com.avs.moviefinder.R
import com.avs.moviefinder.databinding.FragmentHomeBinding
import com.avs.moviefinder.network.ErrorType
import com.avs.moviefinder.ui.BaseFragment
import com.avs.moviefinder.ui.main.MainActivity
import javax.inject.Inject


class HomeFragment : BaseFragment() {

    @Inject
    lateinit var homeViewModel: HomeViewModel

    private lateinit var binding: FragmentHomeBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )
        val root: View = binding.root
        binding.findViewModel = homeViewModel
        binding.lifecycleOwner = this
        val adapter = FindAdapter(
            MovieListener(
                { movieId -> homeViewModel.openMovieDetails(movieId) },
                { movieId -> homeViewModel.shareMovie(movieId) },
                { movieId -> homeViewModel.addToWatched(movieId) },
                { movieId -> homeViewModel.addToWatchLater(movieId) }),
            CategoryClickListener(
                { homeViewModel.onPopularClick() },
                { homeViewModel.onTopRatedClick() }
            )
        )
        binding.rvFindRecyclerView.adapter = adapter
        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        homeViewModel.movies.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })
        homeViewModel.selectedCategory.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.setSelectedCategory(it)
            }
        })
        homeViewModel.shareBody.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) shareMovie(it)
        })
        homeViewModel.isProgressVisible.observe(viewLifecycleOwner, Observer {
            binding.pbFindProgress.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })
        homeViewModel.errorType.observe(viewLifecycleOwner, Observer {
            handleErrorEvent(it)
        })
        return root
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