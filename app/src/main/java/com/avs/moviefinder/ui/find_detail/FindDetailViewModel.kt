package com.avs.moviefinder.ui.find_detail

import androidx.lifecycle.ViewModel
import com.avs.moviefinder.network.ServerApi
import com.avs.moviefinder.utils.RxBus
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class FindDetailViewModel @Inject constructor(
    private val serverApi: ServerApi,
    rxBus: RxBus
) : ViewModel() {

    private var apiDisposable: Disposable? = null
    private var rxBusDisposable: Disposable? = null

    override fun onCleared() {
        apiDisposable?.dispose()
        rxBusDisposable?.dispose()
        super.onCleared()
    }
}