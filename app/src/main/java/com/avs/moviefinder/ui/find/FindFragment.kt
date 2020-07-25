package com.avs.moviefinder.ui.find

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.avs.moviefinder.ui.recycler_view.MovieListener
import com.avs.moviefinder.R
import com.avs.moviefinder.databinding.FragmentFindBinding
import com.avs.moviefinder.network.ErrorType
import com.avs.moviefinder.ui.BaseFragment
import com.avs.moviefinder.ui.main.MainActivity
import javax.inject.Inject


class FindFragment : BaseFragment() {

    @Inject
    lateinit var findViewModel: FindViewModel

    private lateinit var binding: FragmentFindBinding

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
            inflater, R.layout.fragment_find, container, false
        )
        val root: View = binding.root
        binding.findViewModel = findViewModel
        binding.lifecycleOwner = this
        val adapter = FindAdapter(
            MovieListener(
                { movieId -> findViewModel.openMovieDetails(movieId) },
                { movieId -> findViewModel.shareMovie(movieId) },
                { movieId -> findViewModel.addToWatched(movieId) },
                { movieId -> findViewModel.addToWatchLater(movieId) }),
            CategoryClickListener(
                { findViewModel.onPopularClick() },
                { findViewModel.onTopRatedClick() }
            )
        )
        binding.rvFindRecyclerView.adapter = adapter
        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        findViewModel.movies.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })
        findViewModel.selectedCategory.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.setSelectedCategory(it)
            }
        })
        findViewModel.shareBody.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) shareMovie(it)
        })
        findViewModel.isProgressVisible.observe(viewLifecycleOwner, Observer {
            binding.pbFindProgress.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })
        findViewModel.errorType.observe(viewLifecycleOwner, Observer {
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