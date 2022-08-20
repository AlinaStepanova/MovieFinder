package com.avs.moviefinder.ui.movie

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.avs.moviefinder.RxSchedulerRule
import com.avs.moviefinder.data.dto.*
import com.avs.moviefinder.getOrAwaitValue
import com.avs.moviefinder.repository.MovieRepository
import com.avs.moviefinder.utils.CAST_REQUIRED_COUNT
import com.avs.moviefinder.utils.RxBus
import com.avs.moviefinder.utils.SIMILAR_MOVIES_REQUIRED_COUNT
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MovieViewModelTest {

    @get:Rule
    val rxSchedulerRule = RxSchedulerRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @InjectMocks
    lateinit var rxBus: RxBus

    @Mock
    lateinit var repository: MovieRepository

    private lateinit var viewModel: MovieViewModel

    private lateinit var movie: Movie
    private lateinit var credits: List<Cast>
    private lateinit var similar: ArrayList<Result>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = spy(MovieViewModel(repository, rxBus))
        movie = Movie(
            1,
            "Harry Potter and the Philosopher's Stone",
            isFavorite = false,
            isInWatchLater = false
        )
        credits = listOf(
            Cast(1, "Person1"),
            Cast(2, "Person2"),
            Cast(3, "Person3"),
            Cast(4, "Person4"),
            Cast(5, "Person5"),
            Cast(6, "Person6"),
            Cast(7, "Person7"),
            Cast(8, "Person8"),
            Cast(9, "Person9"),
            Cast(10, "Person10"),
            Cast(11, "Person11"),
            Cast(12, "Person12"),
            Cast(13, "Person13"),
            Cast(14, "Person14"),
            Cast(15, "Person15"),
            Cast(16, "Person16"),
            Cast(17, "Person17"),
        )

        similar = arrayListOf(
            Result("1", name = "Name 1"),
            Result("2", name = "Name 2"),
            Result("3", name = "Name 3"),
            Result("4", name = "Name 4"),
            Result("5", name = "Name 5"),
            Result("6", name = "Name 6"),
            Result("7", name = "Name 7"),
            Result("8", name = "Name 8"),
            Result("9", name = "Name 9"),
            Result("10", name = "Name 10"),
            Result("11", name = "Name 11"),
            Result("12", name = "Name 12"),
            Result("13", name = "Name 13")
        )
    }

    @Test
    fun openMovieTest() {
        viewModel.openMovieDetails(null)
        verify(repository, never()).getMovieData(movie)

        viewModel.openMovieDetails(movie)
        verify(repository, atMostOnce()).getMovieData(movie)
    }

    @Test
    fun creditsTest() {
        viewModel.subscribeToEvents(Credits(credits))
        val currentCredits = viewModel.cast.getOrAwaitValue()
        assertTrue(currentCredits.size <= CAST_REQUIRED_COUNT)
    }

    @Test
    fun similarTest() {
        viewModel.subscribeToEvents(Similar(similar))
        val similarMovies = viewModel.similarMovies.getOrAwaitValue()
        assertTrue(similarMovies.size <= SIMILAR_MOVIES_REQUIRED_COUNT)
    }

    @Test
    fun addToWatchLaterTest() {
        viewModel.openMovieDetails(movie)
        viewModel.subscribeToEvents(movie)
        viewModel.addToWatchLater()
        verify(repository, atMostOnce()).insertMovie(movie)
        viewModel.subscribeToEvents(viewModel.movie.getOrAwaitValue() ?: Movie())
        val updatedMovie = viewModel.movie.getOrAwaitValue() ?: Movie()
        assertTrue(updatedMovie.isInWatchLater)
    }

    @Test
    fun removeFromWatchLaterTest() {
        viewModel.openMovieDetails(movie)
        viewModel.subscribeToEvents(movie.copy(isInWatchLater = true))
        viewModel.addToWatchLater()
        verify(repository, atMostOnce()).insertMovie(movie)
        viewModel.subscribeToEvents(viewModel.movie.getOrAwaitValue() ?: Movie())
        val updatedMovie = viewModel.movie.getOrAwaitValue() ?: Movie()
        assertFalse(updatedMovie.isInWatchLater)
    }

    @Test
    fun addToFavoritesTest() {
        viewModel.openMovieDetails(movie)
        viewModel.subscribeToEvents(movie)
        viewModel.addToFavorites()
        verify(repository, atMostOnce()).insertMovie(movie)
        viewModel.subscribeToEvents(viewModel.movie.getOrAwaitValue() ?: Movie())
        val updatedMovie = viewModel.movie.getOrAwaitValue() ?: Movie()
        assertTrue(updatedMovie.isFavorite)
    }

    @Test
    fun removeFromFavoritesTest() {
        viewModel.openMovieDetails(movie.copy(isFavorite = true))
        viewModel.addToWatchLater()
        verify(repository, atMostOnce()).insertMovie(movie)
        viewModel.subscribeToEvents(movie.copy(isFavorite = false))
        val updatedMovie = viewModel.movie.getOrAwaitValue() ?: Movie()
        assertFalse(updatedMovie.isFavorite)
    }

    @Test
    fun movieChangedTest() {
        viewModel.openMovieDetails(movie)
        viewModel.subscribeToEvents(movie)
        viewModel.addToWatchLater()
        viewModel.addToFavorites()
        viewModel.subscribeToEvents(viewModel.movie.getOrAwaitValue() ?: Movie())
        //val isUpdated = viewModel.isInitialMovieUpdated()
        //assertTrue(isUpdated)
    }

    @Test
    fun movieNotChangedTest() {
        viewModel.openMovieDetails(movie)
        viewModel.subscribeToEvents(movie)
        viewModel.addToWatchLater()
        viewModel.addToWatchLater()
        viewModel.subscribeToEvents(viewModel.movie.getOrAwaitValue() ?: Movie())
        //val isUpdated = viewModel.isInitialMovieUpdated()
        //assertFalse(isUpdated)
    }
}