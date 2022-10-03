package com.avs.moviefinder.ui.favorites

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.avs.moviefinder.RxSchedulerRule
import com.avs.moviefinder.collectData
import com.avs.moviefinder.data.dto.FavoritesList
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.getOrAwaitValue
import com.avs.moviefinder.repository.SavedListsRepository
import com.avs.moviefinder.utils.RxBus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.powermock.api.mockito.PowerMockito


@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
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

    private lateinit var movies: PagingData<Movie>
    private lateinit var movie: Movie
    private lateinit var mutableMovies: MutableList<Movie>
    private val dispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(dispatcher)
        viewModel = spy(FavoritesViewModel(rxBus, repository))
        movie = Movie(1, "Harry Potter and the Philosopher's Stone", isFavorite = true)
        mutableMovies = mutableListOf(
            Movie(2, "Harry Potter and the Chamber of Secrets", isFavorite = true),
            Movie(3, "Harry Potter and the Prisoner of Azkaban", isFavorite = true),
            Movie(4, "Harry Potter and the Goblet of Fire", isFavorite = true)
        )
        movies = PagingData.from(
            mutableListOf(
                movie,
                *mutableMovies.toTypedArray()
            )
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialDataTest() {
        val isProgressVisible = viewModel.isProgressVisible.getOrAwaitValue()
        assertTrue(isProgressVisible)
        verify(repository, times(1)).getFavoritesList(viewModel.viewModelScope)
    }

    @Test
    fun getFavoritesFromDBTest() {
        verify(repository, times(1)).getFavoritesList(viewModel.viewModelScope)
        viewModel.subscribeToEvents(FavoritesList(movies))
        runBlocking {
            viewModel.setListItems(movies.collectData())
        }
        val currentMovies: List<Movie> = viewModel.localMovies.getOrAwaitValue()
        assertTrue(currentMovies != PagingData.empty<Movie>())
        runBlocking {
            assertEquals(currentMovies.size, movies.collectData().size)
        }
        val isProgressVisible = viewModel.isProgressVisible.getOrAwaitValue()
        assertFalse(isProgressVisible)
    }

    @Test
    fun updateMovieNeverCalledTest() {
        viewModel.addFavorites(Movie(id = -1))
        verify(repository, never()).updateMovie(Movie(id = -1))
    }

    @Test
    fun removeFromFavoritesTest() {
        viewModel.subscribeToEvents(FavoritesList(movies))
        val movieToRemove = movie
        viewModel.addFavorites(movieToRemove)
        verify(repository, atMostOnce()).updateMovie(movieToRemove)
        runBlocking {
            viewModel.setListItems(mutableMovies)
        }
        val currentMovies = viewModel.localMovies.getOrAwaitValue() ?: emptyList()
        runBlocking {
            assertFalse(currentMovies.contains(movieToRemove))
            assertEquals(currentMovies.size, movies.collectData().size - 1)
        }
    }
//
    @Test
    fun undoRemovingFromFavoritesTest() {
        viewModel.subscribeToEvents(FavoritesList(movies))
        val movieToRemove = movie
        viewModel.addFavorites(movieToRemove)
        verify(repository, atMostOnce()).updateMovie(movieToRemove)
        assertEquals(viewModel.removedMovie.getOrAwaitValue()?.id, movieToRemove.id)
        //assertTrue(viewModel.removedMovieIndex.getOrAwaitValue() != null)

        PowerMockito.doNothing().`when`(viewModel, "startCountdown")

        viewModel.subscribeToEvents(mutableMovies)
        viewModel.undoRemovingMovie()
        viewModel.subscribeToEvents(FavoritesList(movies))

        runBlocking {
            val currentMovies = viewModel.movies.value?.collectData() ?: emptyList()
            assertTrue(currentMovies.contains(movieToRemove))
            assertEquals(currentMovies.size, movies.collectData().size)
        }
    }
//
//    @Test
//    fun addToWatchLaterTest() {
//        viewModel.subscribeToEvents(FavoritesList(movies))
//        val movie = movies[0]
//        viewModel.addToWatchLater(movie.id)
//        verify(repository, atMostOnce()).updateMovie(movie)
//        viewModel.subscribeToEvents(movie)
//        val currentMovies = viewModel.movies.value ?: emptyList()
//
//        assert(currentMovies.filter { it.isInWatchLater }.size == 1)
//        assertTrue(currentMovies.contains(movie))
//        assertEquals(currentMovies.size, movies.size)
//    }
//
//    @Test
//    fun removeFromWatchLaterTest() {
//        movies.map { it.isInWatchLater = true }
//        viewModel.subscribeToEvents(FavoritesList(movies))
//        val movie = movies[0]
//        viewModel.addToWatchLater(movie.id)
//        verify(repository, atMostOnce()).updateMovie(movie)
//        viewModel.subscribeToEvents(movie)
//        val currentMovies = viewModel.movies.value ?: emptyList()
//
//        assert(currentMovies.filter { it.isInWatchLater }.size == movies.size - 1)
//        assertTrue(currentMovies.contains(movie))
//        assertEquals(currentMovies.size, movies.size)
//    }
}