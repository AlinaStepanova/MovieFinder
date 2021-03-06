package com.avs.moviefinder.ui.watch_later

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.avs.moviefinder.R
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.databinding.FragmentWatchLaterBinding
import com.avs.moviefinder.di.factories.ViewModelFactory
import com.avs.moviefinder.ui.BaseFragment
import com.avs.moviefinder.ui.recycler_view.adaptes.MoviesAdapter
import com.avs.moviefinder.ui.recycler_view.MovieListener
import com.avs.moviefinder.utils.buildUndoSnackBarMessage
import com.avs.moviefinder.utils.getIconVisibility
import javax.inject.Inject

class WatchLaterFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var watchLaterViewModel: WatchLaterViewModel

    private var _binding: FragmentWatchLaterBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        watchLaterViewModel =
            ViewModelProvider(this, viewModelFactory).get(WatchLaterViewModel::class.java)
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
        val adapter = MoviesAdapter(
            MovieListener(
                { movie -> startMovieActivity(movie) },
                { movieId -> watchLaterViewModel.shareMovie(movieId) },
                { movieId -> watchLaterViewModel.addFavorites(movieId) }
            ) { movieId -> watchLaterViewModel.addToWatchLater(movieId) }
        )
        watchLaterViewModel.movies.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
            setIconsVisibility(it)
        })
        watchLaterViewModel.shareBody.observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) shareMovie(it)
        })
        watchLaterViewModel.isProgressVisible.observe(viewLifecycleOwner, {
            binding.pbFetchingProgress.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })
        watchLaterViewModel.updateMovieIndex.observe(viewLifecycleOwner, {
            it?.let {
                adapter.notifyItemChanged(it)
            }
        })
        watchLaterViewModel.isInserted.observe(viewLifecycleOwner, {
            it?.let {
                if (!it.first) watchLaterViewModel.updateMovieIndex.value?.let { index ->
                    adapter.notifyItemRemoved(index)
                    showSnackBarWithAction(
                        buildUndoSnackBarMessage(
                            it.second,
                            getString(R.string.deleted_watch_snack_bar_text)
                        )
                    ) { watchLaterViewModel.undoRemovingMovie() }
                } else watchLaterViewModel.updateMovieIndex.value?.let { index ->
                    adapter.notifyItemInserted(index)
                    binding.rvWatchLaterRecyclerView.smoothScrollToPosition(index)
                }
            }
            setIconsVisibility(adapter.currentList)
        })
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