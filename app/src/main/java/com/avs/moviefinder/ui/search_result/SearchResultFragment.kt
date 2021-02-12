package com.avs.moviefinder.ui.search_result

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
import com.avs.moviefinder.R
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.network.ErrorType
import com.avs.moviefinder.databinding.FragmentFindDetailBinding
import com.avs.moviefinder.di.ViewModelFactory
import com.avs.moviefinder.ui.BaseFragment
import com.avs.moviefinder.ui.MOVIE_EXTRA_TAG
import com.avs.moviefinder.ui.movie.MovieActivity
import com.avs.moviefinder.ui.recycler_view.BaseMoviesAdapter
import com.avs.moviefinder.ui.recycler_view.MovieListener
import com.avs.moviefinder.utils.ConnectionLiveData
import javax.inject.Inject

val FIND_DETAIL_FRAGMENT_TAG = FindDetailFragment::class.simpleName

class FindDetailFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var connectionLiveData: ConnectionLiveData
    lateinit var searchResultViewModel: SearchResultViewModel

    private lateinit var binding: FragmentFindDetailBinding

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
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_find_detail, container, false
        )
        val root: View = binding.root
        binding.findDetailViewModel = searchResultViewModel
        binding.lifecycleOwner = this
        val query = arguments?.getString(this::class.java.simpleName)
        searchResultViewModel.searchInitialQuery(query)
        val adapter = BaseMoviesAdapter(
            MovieListener(
                { movie -> startMovieActivityForResult(movie) },
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
        connectionLiveData.observe(viewLifecycleOwner, {
            searchResultViewModel.reactOnNetworkChangeState(it)
        })
        searchResultViewModel.isBackOnline.observe(viewLifecycleOwner, {
            it?.let {
                showConnectivitySnackBar(getString(R.string.back_online_text))
            }
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