package com.avs.moviefinder.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.avs.moviefinder.data.dto.ConnectivityRestored
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.dto.PagingDataList
import com.avs.moviefinder.data.network.ErrorType
import com.avs.moviefinder.repository.HomeRepository
import com.avs.moviefinder.utils.RxBus
import com.avs.moviefinder.utils.buildShareLink
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    rxBus: RxBus,
    private val homeRepository: HomeRepository
) : ViewModel() {

    private var _movies = MutableLiveData<PagingData<Movie>>()
    val movies: LiveData<PagingData<Movie>>
        get() = _movies

    private var _isProgressVisible = MutableLiveData<Boolean>()
    val isProgressVisible: LiveData<Boolean>
        get() = _isProgressVisible
    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading
    private var _errorType = MutableLiveData<ErrorType?>()
    val errorType: LiveData<ErrorType?>
        get() = _errorType
    private var _shareBody = MutableLiveData<String?>()
    val shareBody: LiveData<String?>
        get() = _shareBody
    private var _selectedCategory = MutableLiveData<MoviesCategory>()
    val selectedCategory: LiveData<MoviesCategory>
        get() = _selectedCategory
    private val compositeDisposable = CompositeDisposable()

    init {
        _selectedCategory.value = MoviesCategory.POPULAR
        compositeDisposable.add(rxBus.events.subscribe { event -> subscribeToEvents(event) })
        homeRepository.getAllMovies(_selectedCategory.value, viewModelScope)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        homeRepository.clear()
        super.onCleared()
    }

    private fun subscribeToEvents(event: Any?) {
        when (event) {
            is Locale -> onRefresh()
            is ConnectivityRestored -> {
                if (_errorType.value == ErrorType.NETWORK) onRefresh()
            }
            is Throwable -> {
                _isProgressVisible.value = false
                _isLoading.value = false
                _movies.value = PagingData.empty()
                _errorType.value = ErrorType.NETWORK
            }
            is PagingDataList -> {
                _isProgressVisible.value = false
                _isLoading.value = false
                _movies.value = event.movies
            }
        }
    }

    fun onRefresh() {
        _errorType.value = null
        homeRepository.getAllMovies(_selectedCategory.value, viewModelScope)
    }

    fun shareMovie(movieId: Long) {
        _shareBody.value = buildShareLink(movieId)
        _shareBody.value = null
    }

    fun onPopularClick() {
        if (_selectedCategory.value != MoviesCategory.POPULAR) {
            _selectedCategory.value = MoviesCategory.POPULAR
            onRefresh()
        }
    }

    fun onTopRatedClick() {
        if (_selectedCategory.value != MoviesCategory.TOP_RATED) {
            _selectedCategory.value = MoviesCategory.TOP_RATED
            onRefresh()
        }
    }

    fun onNowPlayingClick() {
        if (_selectedCategory.value != MoviesCategory.NOW_PLAYING) {
            _selectedCategory.value = MoviesCategory.NOW_PLAYING
            onRefresh()
        }
    }
}