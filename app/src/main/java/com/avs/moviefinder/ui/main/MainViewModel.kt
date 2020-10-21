package com.avs.moviefinder.ui.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.avs.moviefinder.data.dto.Query
import com.avs.moviefinder.utils.RxBus
import io.reactivex.disposables.Disposable

class MainViewModel constructor(
    private val rxBus: RxBus,
    private val handle: SavedStateHandle
) : ViewModel() {

    companion object {
        val KEY_SEARCH_QUERY = "KEY_SEARCH_QUERY"
    }

    private var apiDisposable: Disposable? = null

    override fun onCleared() {
        apiDisposable?.dispose()
        super.onCleared()
    }

    fun onQueryTextSubmit(newText: String) {
        rxBus.send(Query(newText))
    }

    fun onQueryTextChanged(newText: String) = handle.set(KEY_SEARCH_QUERY, newText)
    fun getLatestQueryTest(): String? = handle.get<String>(KEY_SEARCH_QUERY)
}