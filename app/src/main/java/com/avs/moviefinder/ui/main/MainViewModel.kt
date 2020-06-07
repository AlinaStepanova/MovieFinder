package com.avs.moviefinder.ui.main

import androidx.lifecycle.ViewModel
import com.avs.moviefinder.network.ServerApi
import com.avs.moviefinder.utils.RxBus
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val serverApi: ServerApi,
    rxBus: RxBus
) : ViewModel() {

    private var apiDisposable: Disposable? = null

    override fun onCleared() {
        apiDisposable?.dispose()
        super.onCleared()
    }

    fun onQuerySubmitted(query: String) {
        apiDisposable = serverApi.getMovieByTitle(query)
    }

    fun onQueryTextChange(newText: String) {

    }
}