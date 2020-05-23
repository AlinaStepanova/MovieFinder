package com.avs.moviefinder.ui.watch_later

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Inject

class WatchLaterViewModel @Inject constructor() : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Watch later Fragment"
    }
    val text: LiveData<String> = _text
}