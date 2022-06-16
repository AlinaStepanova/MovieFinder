package com.avs.moviefinder.ui.watch_later

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.avs.moviefinder.RxSchedulerRule
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.dto.WatchList
import com.avs.moviefinder.getOrAwaitValue
import com.avs.moviefinder.repository.SavedListsRepository
import com.avs.moviefinder.utils.RxBus
import junit.framework.Assert.assertTrue
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.powermock.api.mockito.PowerMockito

@RunWith(MockitoJUnitRunner::class)
internal class WatchLaterViewModelTest {

    @get:Rule
    val rxSchedulerRule = RxSchedulerRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @InjectMocks
    lateinit var rxBus: RxBus

    @Mock
    lateinit var repository: SavedListsRepository

    private lateinit var viewModel: WatchLaterViewModel

    private lateinit var movies: MutableList<Movie>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = Mockito.spy(WatchLaterViewModel(rxBus, repository))
        movies = mutableListOf(
            Movie(1, "Harry Potter and the Philosopher's Stone", isInWatchLater = true),
            Movie(2, "Harry Potter and the Chamber of Secrets", isInWatchLater = true),
            Movie(3, "Harry Potter and the Prisoner of Azkaban", isInWatchLater = true),
            Movie(4, "Harry Potter and the Goblet of Fire", isInWatchLater = true),
            Movie(5, "Harry Potter and the Order of the Phoenix", isInWatchLater = true)
        )
    }

    @Test
    fun initialDataTest() {
        val isProgressVisible = viewModel.isProgressVisible.getOrAwaitValue()
        Assert.assertTrue(isProgressVisible)
        Mockito.verify(repository, Mockito.times(1)).getWatchList()
    }

    @Test
    fun getWatchlistFromDBTest() {
        var currentMovies = viewModel.movies.value
        Assert.assertTrue(currentMovies.isNullOrEmpty())
        Mockito.verify(repository, Mockito.times(1)).getWatchList()
        viewModel.subscribeToEvents(WatchList(movies))
        currentMovies = viewModel.movies.getOrAwaitValue()
        Assert.assertTrue(currentMovies.isNotEmpty())
        assertEquals(currentMovies.size, movies.size)
        val isProgressVisible = viewModel.isProgressVisible.getOrAwaitValue()
        assertFalse(isProgressVisible)
    }

    @Test
    fun updateMovieNeverCalledTest() {
        viewModel.addToWatchLater(-1)
        viewModel.addToWatchLater(100)
        Mockito.verify(repository, Mockito.never()).updateMovie(movies[0])
    }

    @Test
    fun removeFromWatchlistTest() {
        viewModel.subscribeToEvents(WatchList(movies))
        val movieToRemove = movies[0]
        viewModel.addToWatchLater(movieToRemove.id)
        Mockito.verify(repository, Mockito.atMostOnce()).updateMovie(movieToRemove)
        viewModel.subscribeToEvents(movieToRemove)
        val currentMovies = viewModel.movies.value ?: emptyList()
        assertFalse(currentMovies.contains(movieToRemove))
        assertEquals(currentMovies.size, movies.size - 1)
    }

    @Test
    fun undoRemovingFromWatchlistTest() {
        viewModel.subscribeToEvents(WatchList(movies))
        val movieToRemove = movies[1]
        viewModel.addToWatchLater(movieToRemove.id)
        Mockito.verify(repository, Mockito.atMostOnce()).updateMovie(movieToRemove)

        PowerMockito.doNothing().`when`(viewModel, "startCountdown")
        viewModel.subscribeToEvents(movieToRemove)
        viewModel.undoRemovingMovie()
        viewModel.subscribeToEvents(movieToRemove)

        val currentMovies = viewModel.movies.value ?: emptyList()
        assertTrue(currentMovies.contains(movieToRemove))
        assertEquals(currentMovies.size, movies.size)
    }

    @Test
    fun addToFavoritesTest() {
        viewModel.subscribeToEvents(WatchList(movies))
        val movie = movies[0]
        viewModel.addFavorites(movie.id)
        Mockito.verify(repository, Mockito.atMostOnce()).updateMovie(movie)
        viewModel.subscribeToEvents(movie)
        val currentMovies = viewModel.movies.value ?: emptyList()

        assert(currentMovies.filter { it.isFavorite }.size == 1)
        assertTrue(currentMovies.contains(movie))
        assertEquals(currentMovies.size, movies.size)
    }

    @Test
    fun removeFromFavoritesTest() {
        movies.map { it.isFavorite = true }
        viewModel.subscribeToEvents(WatchList(movies))
        val movie = movies[0]
        viewModel.addFavorites(movie.id)
        Mockito.verify(repository, Mockito.atMostOnce()).updateMovie(movie)
        viewModel.subscribeToEvents(movie)
        val currentMovies = viewModel.movies.value ?: emptyList()

        assert(currentMovies.filter { it.isFavorite }.size == movies.size - 1)
        Assert.assertTrue(currentMovies.contains(movie))
        assertEquals(currentMovies.size, movies.size)
    }
}