package com.avs.moviefinder.ui.search_result

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.avs.moviefinder.R
import com.avs.moviefinder.data.network.ErrorType
import com.avs.moviefinder.databinding.FragmentSearchResultBinding
import com.avs.moviefinder.di.factories.ViewModelFactory
import com.avs.moviefinder.ui.BaseFragment
import com.avs.moviefinder.ui.recycler_view.MovieListener
import com.avs.moviefinder.ui.recycler_view.adaptes.MoviesAdapter
import javax.inject.Inject

class SearchResultFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var searchResultViewModel: SearchResultViewModel
    private val args: SearchResultFragmentArgs by navArgs()

    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let {
                    searchResultViewModel.handleOnActivityResult(it)
                }
            }
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        searchResultViewModel =
            ViewModelProvider(this, viewModelFactory).get(SearchResultViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_search_result, container, false
        )
        val root: View = binding.root
        binding.findDetailViewModel = searchResultViewModel
        binding.lifecycleOwner = this
        searchResultViewModel.searchInitialQuery(args.query)
        val adapter = MoviesAdapter(
            MovieListener(
                { movie -> startMovieActivityForResult(movie, resultLauncher) },
                { movieId -> searchResultViewModel.shareMovie(movieId) },
                { movieId -> searchResultViewModel.addToFavorites(movieId) }
            ) { movieId -> searchResultViewModel.addToWatchLater(movieId) }
        )
        binding.rvFindRecyclerView.adapter = adapter
        searchResultViewModel.movies.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })
        searchResultViewModel.updateMovieIndex.observe(viewLifecycleOwner, {
            it?.let {
                adapter.notifyItemChanged(it)
            }
        })
        searchResultViewModel.shareBody.observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) shareMovie(it)
        })
        searchResultViewModel.isProgressVisible.observe(viewLifecycleOwner, {
            binding.pbFindProgress.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })
        searchResultViewModel.errorType.observe(viewLifecycleOwner, {
            handleErrorEvent(it)
        })
        return root
    }

    override fun onResume() {
        super.onResume()
        searchResultViewModel.subscribeToEvents()
    }

    override fun onPause() {
        searchResultViewModel.unsubscribeFromEvents()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleErrorEvent(it: ErrorType?) {
        when (it) {
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
                binding.ivError.visibility = View.INVISIBLE
                binding.tvErrorText.visibility = View.INVISIBLE
            }
        }
    }
}