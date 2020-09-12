package com.avs.moviefinder.ui.main

import androidx.lifecycle.ViewModel
import com.avs.moviefinder.data.dto.Query
import com.avs.moviefinder.utils.RxBus
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val rxBus: RxBus
) : ViewModel() {

    private var apiDisposable: Disposable? = null

    override fun onCleared() {
        apiDisposable?.dispose()
        super.onCleared()
    }

    fun onQueryTextSubmit(newText: String) {
        rxBus.send(Query(newText))
        // todo fix rotation search bar issue
    }
}