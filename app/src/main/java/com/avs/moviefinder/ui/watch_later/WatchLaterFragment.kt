package com.avs.moviefinder.ui.watch_later

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.avs.moviefinder.R
import com.avs.moviefinder.databinding.FragmentWatchLaterBinding
import com.avs.moviefinder.di.ViewModelFactory
import com.avs.moviefinder.ui.BaseFragment
import com.avs.moviefinder.ui.recycler_view.BaseMoviesAdapter
import com.avs.moviefinder.ui.recycler_view.MovieListener
import javax.inject.Inject

class WatchLaterFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var watchLaterViewModel: WatchLaterViewModel

    private lateinit var binding: FragmentWatchLaterBinding

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
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_watch_later, container, false
        )
        val root: View = binding.root
        binding.watchLaterViewModel = watchLaterViewModel
        binding.lifecycleOwner = this
        val adapter = BaseMoviesAdapter(
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
                if (!it) watchLaterViewModel.updateMovieIndex.value?.let { index ->
                    adapter.notifyItemRemoved(index)
                    showSnackBarWithAction(
                        getString(R.string.deleted_watch_snack_bar_text)
                    ) { watchLaterViewModel.undoRemovingMovie() }
                } else watchLaterViewModel.updateMovieIndex.value?.let { index ->
                    adapter.notifyItemInserted(index)
                    binding.rvWatchLaterRecyclerView.smoothScrollToPosition(index)
                }
            }
        })
        binding.rvWatchLaterRecyclerView.adapter = adapter
        watchLaterViewModel.fetchWatchLaterList()
        return root
    }
}