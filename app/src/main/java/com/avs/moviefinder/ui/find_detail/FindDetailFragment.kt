package com.avs.moviefinder.ui.find_detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.avs.moviefinder.MovieListener
import com.avs.moviefinder.R
import com.avs.moviefinder.databinding.FragmentFindDetailBinding
import com.avs.moviefinder.network.ErrorType
import com.avs.moviefinder.ui.BaseFragment
import com.avs.moviefinder.ui.find.FindAdapter
import com.avs.moviefinder.ui.main.MainActivity
import javax.inject.Inject

val FIND_DETAIL_FRAGMENT_TAG = FindDetailFragment::class.simpleName

class FindDetailFragment : BaseFragment() {

    @Inject
    lateinit var findDetailViewModel: FindDetailViewModel

    private lateinit var binding: FragmentFindDetailBinding

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
            inflater, R.layout.fragment_find_detail, container, false
        )
        val root: View = binding.root
        binding.findDetailViewModel = findDetailViewModel
        binding.lifecycleOwner = this
        val adapter = FindAdapter(
            MovieListener(
                { movieId -> findDetailViewModel.openMovieDetails(movieId) },
                { movieId -> findDetailViewModel.shareMovie(movieId) },
                { movieId -> findDetailViewModel.addToWatched(movieId) },
                { movieId -> findDetailViewModel.addToWatchLater(movieId) })
        )
        binding.rvFindRecyclerView.adapter = adapter
        findDetailViewModel.movies.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })
        findDetailViewModel.isProgressVisible.observe(viewLifecycleOwner, Observer {
            binding.pbFindProgress.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })
        findDetailViewModel.errorType.observe(viewLifecycleOwner, Observer {
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