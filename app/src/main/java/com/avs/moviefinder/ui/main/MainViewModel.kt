package com.avs.moviefinder.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.avs.moviefinder.data.dto.ConnectivityRestored
import com.avs.moviefinder.data.dto.Query
import com.avs.moviefinder.utils.RxBus

class MainViewModel constructor(
    private val rxBus: RxBus,
    private val handle: SavedStateHandle
) : ViewModel() {

    private var _isBackOnline = MutableLiveData<Boolean?>()
    val isBackOnline: LiveData<Boolean?>
        get() = _isBackOnline

    init {
        _isBackOnline.value = null
    }

    fun onQueryTextSubmit(newText: String) {
        rxBus.send(Query(newText))
    }

    fun reactOnNetworkChangeState(isActive: Boolean) {
        if (isActive && _isBackOnline.value == false) {
            _isBackOnline.value = true
            _isBackOnline.value = null
            rxBus.send(ConnectivityRestored())
        } else if (!isActive) {
            _isBackOnline.value = false
        }
    }

    fun onQueryTextChanged(newText: String) = handle.set(KEY_SEARCH_QUERY, newText)
    fun getLatestQueryTest(): String? = handle.get<String>(KEY_SEARCH_QUERY)

    companion object {
        const val KEY_SEARCH_QUERY = "KEY_SEARCH_QUERY"
    }
}