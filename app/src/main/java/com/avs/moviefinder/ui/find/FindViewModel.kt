package com.avs.moviefinder.ui.find

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avs.moviefinder.network.ServerApi
import com.avs.moviefinder.utils.RxBus
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class FindViewModel @Inject constructor(
    private val serverApi: ServerApi,
    rxBus: RxBus
) : ViewModel() {

    private var apiDisposable: Disposable? = null

    init {
        apiDisposable = serverApi.getPopularMovies()
    }

    override fun onCleared() {
        apiDisposable?.dispose()
        super.onCleared()
    }
}