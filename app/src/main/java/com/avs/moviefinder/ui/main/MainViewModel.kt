package com.avs.moviefinder.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.avs.moviefinder.network.ServerApi
import com.avs.moviefinder.utils.RxBus
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val serverApi: ServerApi,
    rxBus: RxBus
) : ViewModel() {
    init {
        Log.d("MainViewModel", "MainViewModel")
    }
}