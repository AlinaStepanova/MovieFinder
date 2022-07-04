package com.avs.moviefinder.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.navGraphViewModels
import androidx.paging.LoadState
import com.avs.moviefinder.R
import com.avs.moviefinder.data.network.ErrorType
import com.avs.moviefinder.databinding.FragmentHomeBinding
import com.avs.moviefinder.di.factories.ViewModelFactory
import com.avs.moviefinder.ui.BaseFragment
import com.avs.moviefinder.ui.recycler_view.MovieClickListener
import com.avs.moviefinder.ui.recycler_view.adaptes.MoviePreviewPagingAdapter
import com.avs.moviefinder.utils.ConnectionLiveData
import javax.inject.Inject


class HomeFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var connectionLiveData: ConnectionLiveData

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by
    navGraphViewModels(R.id.nav_graph) {
        viewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )
        val root: View = binding.root
        binding.findViewModel = homeViewModel
        binding.lifecycleOwner = this
        homeViewModel.selectedCategory.value?.let {
            binding.selectedCategory = it
        }

        binding.clickListener = CategoryClickListener(
            { homeViewModel.onPopularClick() },
            { homeViewModel.onTopRatedClick() },
            { homeViewModel.onNowPlayingClick() },
            { homeViewModel.onUpcomingClick() }
        )
        val adapter = MoviePreviewPagingAdapter(
            MovieClickListener { movie -> startMovieActivity(movie) }
        )

        adapter.addLoadStateListener { loadState ->
            if (loadState.source.refresh is LoadState.Error) {
                handleErrorEvent(ErrorType.NO_RESULTS)
            }
        }

        binding.rvFindRecyclerView.adapter = adapter
        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        homeViewModel.movies.observe(viewLifecycleOwner) { movies ->
            adapter.submitData(lifecycle, movies)
        }
        homeViewModel.selectedCategory.observe(viewLifecycleOwner) {
            it?.let {
                binding.selectedCategory = it
            }
        }
        homeViewModel.shareBody.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) shareMovie(it)
        }
        homeViewModel.isProgressVisible.observe(viewLifecycleOwner) {
            binding.pbFindProgress.visibility = if (it) View.VISIBLE else View.INVISIBLE
        }
        homeViewModel.errorType.observe(viewLifecycleOwner) {
            handleErrorEvent(it)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleErrorEvent(errorType: ErrorType?) {
        when (errorType) {
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