package com.avs.moviefinder.ui.favorites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import com.avs.moviefinder.R
import com.avs.moviefinder.databinding.FragmentFavoritesBinding
import com.avs.moviefinder.di.factories.ViewModelFactory
import com.avs.moviefinder.ui.BaseFragment
import com.avs.moviefinder.ui.recycler_view.MovieListener
import com.avs.moviefinder.ui.recycler_view.adaptes.MoviesPagingAdapter
import com.avs.moviefinder.utils.buildUndoSnackBarMessage
import com.avs.moviefinder.utils.getIconVisibility
import javax.inject.Inject

class FavoritesFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding

    private val favoritesViewModel: FavoritesViewModel by
    navGraphViewModels(R.id.nav_graph) {
        viewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_favorites, container, false
        )
        val root: View = binding!!.root
        binding?.favoritesViewModel = favoritesViewModel
        binding?.lifecycleOwner = this
        val adapter = MoviesPagingAdapter(
            MovieListener(
                { movie -> startMovieActivity(movie) },
                { movieId -> favoritesViewModel.shareMovie(movieId) },
                { movie -> favoritesViewModel.addFavorites(movie) }
            ) { movie -> favoritesViewModel.addToWatchLater(movie) }
        )
        favoritesViewModel.movies.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitData(lifecycle, it)
            }
        }

        adapter.addLoadStateListener {
            setIconsVisibility(adapter.itemCount)
            favoritesViewModel.setListItems(adapter.snapshot().items)
        }

        favoritesViewModel.shareBody.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) shareMovie(it)
        }
        favoritesViewModel.isProgressVisible.observe(viewLifecycleOwner) {
            binding?.pbFetchingProgress?.visibility = if (it) View.VISIBLE else View.INVISIBLE
        }
        favoritesViewModel.removedMovieIndex.observe(viewLifecycleOwner) { position ->
            Log.d("jjj", "pos " + position.toString())
            position?.let {
                if (it.first && it.second != -1) {
                    binding?.rvFindRecyclerView?.postDelayed({
                        binding?.rvFindRecyclerView?.smoothScrollToPosition(
                            it.second
                        )
                        favoritesViewModel.disposeUndoDependencies()
                    }, 100)
                }
            }
        }
        favoritesViewModel.removedMovie.observe(viewLifecycleOwner) { movie ->
            movie?.title?.let { title ->
                showSnackBarWithAction(
                    buildUndoSnackBarMessage(
                        title,
                        getString(R.string.deleted_favorite_snack_bar_text)
                    )
                ) { favoritesViewModel.undoRemovingMovie() }
            }
        }
        binding?.rvFindRecyclerView?.adapter = adapter
        ItemTouchHelper(itemTouchCallback(favoritesViewModel::removeFromFavorites))
            .attachToRecyclerView(binding?.rvFindRecyclerView)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setIconsVisibility(moviesCount: Int) {
        binding?.ivMovieIcon?.visibility = getIconVisibility(moviesCount)
        binding?.ivFavoriteIcon?.visibility = getIconVisibility(moviesCount)
    }
}