package com.avs.moviefinder.ui.favorites

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.avs.moviefinder.RxSchedulerRule
import com.avs.moviefinder.data.dto.FavoritesList
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.getOrAwaitValue
import com.avs.moviefinder.repository.SavedListsRepository
import com.avs.moviefinder.utils.RxBus
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class FavoritesViewModelTest {

    @get:Rule
    val rxSchedulerRule = RxSchedulerRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @InjectMocks
    lateinit var rxBus: RxBus

    @Mock
    lateinit var repository: SavedListsRepository

    private lateinit var viewModel: FavoritesViewModel

    private lateinit var movies: MutableList<Movie>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = FavoritesViewModel(rxBus, repository)
        movies = mutableListOf(
            Movie(1, "Harry Potter and the Philosopher's Stone"),
            Movie(2, "Harry Potter and the Chamber of Secrets"),
            Movie(3, "Harry Potter and the Prisoner of Azkaban"),
            Movie(4, "Harry Potter and the Goblet of Fire")
        )
    }

    @Test
    fun initialDataTest() {
        val isProgressVisible = viewModel.isProgressVisible.getOrAwaitValue()
        assertTrue(isProgressVisible)
        verify(repository, times(1)).getFavoritesList()
    }

    @Test
    fun getFavoritesFromDBTest() {
        var currentMovie = viewModel.movies.value
        assertTrue(currentMovie.isNullOrEmpty())
        verify(repository, times(1)).getFavoritesList()
        viewModel.subscribeToEvents(FavoritesList(movies))
        currentMovie = viewModel.movies.getOrAwaitValue()
        assertTrue(currentMovie.isNotEmpty())
        assertEquals(currentMovie.size, movies.size)
        val isProgressVisible = viewModel.isProgressVisible.getOrAwaitValue()
        assertFalse(isProgressVisible)
    }
}