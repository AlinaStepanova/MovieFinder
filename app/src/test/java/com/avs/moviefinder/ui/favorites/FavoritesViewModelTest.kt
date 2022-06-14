package com.avs.moviefinder.ui.favorites

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.avs.moviefinder.RxSchedulerRule
import com.avs.moviefinder.getOrAwaitValue
import com.avs.moviefinder.repository.SavedListsRepository
import com.avs.moviefinder.utils.RxBus
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
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

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = FavoritesViewModel(rxBus, repository)
    }

    @Test
    fun initialDataTest() {
        val isProgressVisible = viewModel.isProgressVisible.getOrAwaitValue()
        Assert.assertTrue(isProgressVisible)
    }
}