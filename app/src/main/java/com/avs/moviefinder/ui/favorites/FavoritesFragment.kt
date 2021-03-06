package com.avs.moviefinder.ui.favorites

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.avs.moviefinder.R
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.databinding.FragmentFavoritesBinding
import com.avs.moviefinder.di.factories.ViewModelFactory
import com.avs.moviefinder.ui.BaseFragment
import com.avs.moviefinder.ui.recycler_view.adaptes.MoviesAdapter
import com.avs.moviefinder.ui.recycler_view.MovieListener
import com.avs.moviefinder.utils.buildUndoSnackBarMessage
import com.avs.moviefinder.utils.getIconVisibility
import javax.inject.Inject

class FavoritesFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var favoritesViewModel: FavoritesViewModel

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        favoritesViewModel =
            ViewModelProvider(this, viewModelFactory).get(FavoritesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_favorites, container, false
        )
        val root: View = binding.root
        binding.favoritesViewModel = favoritesViewModel
        binding.lifecycleOwner = this
        val adapter = MoviesAdapter(
            MovieListener(
                { movie -> startMovieActivity(movie) },
                { movieId -> favoritesViewModel.shareMovie(movieId) },
                { movieId -> favoritesViewModel.addFavorites(movieId) }
            ) { movieId -> favoritesViewModel.addToWatchLater(movieId) }
        )
        favoritesViewModel.movies.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
            setIconsVisibility(it)
        })
        favoritesViewModel.shareBody.observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) shareMovie(it)
        })
        favoritesViewModel.isProgressVisible.observe(viewLifecycleOwner, {
            binding.pbFetchingProgress.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })
        favoritesViewModel.updateMovieIndex.observe(viewLifecycleOwner, {
            it?.let {
                adapter.notifyItemChanged(it)
            }
        })
        favoritesViewModel.isInserted.observe(viewLifecycleOwner, {
            it?.let {
                if (!it.first) favoritesViewModel.updateMovieIndex.value?.let { index ->
                    adapter.notifyItemRemoved(index)
                    showSnackBarWithAction(
                        buildUndoSnackBarMessage(
                            it.second,
                            getString(R.string.deleted_favorite_snack_bar_text)
                        )
                    ) { favoritesViewModel.undoRemovingMovie() }
                } else favoritesViewModel.updateMovieIndex.value?.let { index ->
                    adapter.notifyItemInserted(index)
                    binding.rvFindRecyclerView.smoothScrollToPosition(index)
                }
            }
            setIconsVisibility(adapter.currentList)
        })
        binding.rvFindRecyclerView.adapter = adapter
        favoritesViewModel.getFavorites()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setIconsVisibility(movies: List<Movie>) {
        binding.ivMovieIcon.visibility = getIconVisibility(movies)
        binding.ivFavoriteIcon.visibility = getIconVisibility(movies)
    }
}