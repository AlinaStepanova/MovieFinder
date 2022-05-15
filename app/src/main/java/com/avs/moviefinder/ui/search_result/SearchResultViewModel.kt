package com.avs.moviefinder.ui.search_result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.avs.moviefinder.data.dto.ConnectivityRestored
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.dto.PagingDataList
import com.avs.moviefinder.data.dto.Query
import com.avs.moviefinder.data.network.ErrorType
import com.avs.moviefinder.repository.SearchResultRepository
import com.avs.moviefinder.utils.RxBus
import io.reactivex.disposables.Disposable
import java.util.*
import javax.inject.Inject

class SearchResultViewModel @Inject constructor(
    private val rxBus: RxBus,
    private val searchResultRepository: SearchResultRepository
) : ViewModel() {

    private var _movies = MutableLiveData<PagingData<Movie>>()
    val movies: LiveData<PagingData<Movie>>
        get() = _movies
    private var _isProgressVisible = MutableLiveData<Boolean>()
    val isProgressVisible: LiveData<Boolean>
        get() = _isProgressVisible
    private var _isLoading = MutableLiveData<Boolean>()
    private var _errorType = MutableLiveData<ErrorType?>()
    val errorType: LiveData<ErrorType?>
        get() = _errorType
    private var _shareBody = MutableLiveData<String?>()
    val shareBody: LiveData<String?>
        get() = _shareBody
    private var _query = MutableLiveData<String?>()
    private var _initialQuery = MutableLiveData<String?>()
    private var rxBusDisposable: Disposable? = null

    init {
        setLoadingState()
    }

    override fun onCleared() {
        unsubscribeFromEvents()
        searchResultRepository.clear()
        super.onCleared()
    }

    private fun handleServerResponse(event: Any?) {
        when (event) {
            is Query -> onQuerySubmitted(event.query)
            is Locale -> getQueryByTitle(_query.value)
            is ConnectivityRestored -> {
                if (_errorType.value == ErrorType.NETWORK) getQueryByTitle(_query.value)
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

    private fun onQuerySubmitted(query: String?) {
        if (query != null) {
            setLoadingState()
            searchResultRepository.getAllPagedMovies(query, viewModelScope)
            _query.value = query
        }
    }

    private fun setLoadingState() {
        _isProgressVisible.value = true
        _errorType.value = null
        _movies.value = PagingData.empty()
    }

    private fun getQueryByTitle(query: String?) {
        if (query != null) {
            setLoadingState()
            searchResultRepository.getAllPagedMovies(_query.value ?: "", viewModelScope)
        }
    }

    fun searchInitialQuery(query: String?) {
        if (_initialQuery.value == null) {
            _initialQuery.value = query
            onQuerySubmitted(query)
        }
    }

    fun subscribeToEvents() {
        unsubscribeFromEvents()
        rxBusDisposable = rxBus.events.subscribe { event -> handleServerResponse(event) }
    }

    fun unsubscribeFromEvents() {
        rxBusDisposable?.dispose()
    }

}