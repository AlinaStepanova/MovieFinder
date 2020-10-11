package com.avs.moviefinder.di

import androidx.lifecycle.SavedStateHandle
import com.avs.moviefinder.ui.main.MainViewModel
import com.avs.moviefinder.utils.RxBus
import javax.inject.Inject

class MainViewModelFactory @Inject constructor(
    val rxBus: RxBus
) : ViewModelAssistedFactory<MainViewModel> {
    override fun create(handle: SavedStateHandle): MainViewModel {
        return MainViewModel(rxBus, handle)
    }
}