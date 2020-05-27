package com.avs.moviefinder.di

import android.content.Context
import com.avs.moviefinder.network.ServerApi
import com.avs.moviefinder.utils.RxBus
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [NetworkModule::class, AppSubcomponents::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun mainComponent(): MainComponent.Factory
    fun serverApi() : ServerApi
    fun rxBus() : RxBus
}
