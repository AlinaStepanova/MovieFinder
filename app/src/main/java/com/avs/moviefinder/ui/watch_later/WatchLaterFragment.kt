package com.avs.moviefinder.ui.watch_later

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.navGraphViewModels
import com.avs.moviefinder.R
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.databinding.FragmentWatchLaterBinding
import com.avs.moviefinder.di.factories.ViewModelFactory
import com.avs.moviefinder.ui.BaseFragment
import com.avs.moviefinder.ui.recycler_view.MovieListener
import com.avs.moviefinder.ui.recycler_view.adaptes.MoviesPagingAdapter
import com.avs.moviefinder.utils.buildUndoSnackBarMessage
import com.avs.moviefinder.utils.getIconVisibility
import javax.inject.Inject

class WatchLaterFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var _binding: FragmentWatchLaterBinding? = null
    private val binding get() = _binding!!

    private val watchLaterViewModel: WatchLaterViewModel by
    navGraphViewModels(R.id.nav_graph) {
        viewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_watch_later, container, false
        )
        val root: View = binding.root
        binding.watchLaterViewModel = watchLaterViewModel
        binding.lifecycleOwner = this
        val adapter = MoviesPagingAdapter(
            MovieListener(
                { movie -> startMovieActivity(movie) },
                { movieId -> watchLaterViewModel.shareMovie(movieId) },
                { movie -> watchLaterViewModel.addFavorites(movie) }
            ) { movie -> watchLaterViewModel.addToWatchLater(movie) }
        )
        watchLaterViewModel.movies.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitData(lifecycle, it)
            }
            setIconsVisibility(adapter.snapshot().items)
        }
        watchLaterViewModel.shareBody.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) shareMovie(it)
        }
        watchLaterViewModel.isProgressVisible.observe(viewLifecycleOwner) {
            binding.pbFetchingProgress.visibility = if (it) View.VISIBLE else View.INVISIBLE
        }
        watchLaterViewModel.removedMovie.observe(viewLifecycleOwner) { movie ->
            movie?.title?.let { title ->
                showSnackBarWithAction(
                    buildUndoSnackBarMessage(
                        title,
                        getString(R.string.deleted_favorite_snack_bar_text)
                    )
                ) { watchLaterViewModel.undoRemovingMovie() }
            }
            setIconsVisibility(adapter.snapshot().items)
        }
        binding.rvWatchLaterRecyclerView.adapter = adapter
        watchLaterViewModel.getWatchLaterMovies()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setIconsVisibility(movies: List<Movie>) {
        binding.ivMovieIcon.visibility = getIconVisibility(movies)
        binding.ivWatchLaterIcon.visibility = getIconVisibility(movies)
    }
}