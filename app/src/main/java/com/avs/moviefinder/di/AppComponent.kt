package com.avs.moviefinder.di

import android.app.Application
import com.avs.moviefinder.MovieFinderApplication
import com.avs.moviefinder.data.database.DatabaseManager
import com.avs.moviefinder.data.network.ServerApi
import com.avs.moviefinder.di.modules.*
import com.avs.moviefinder.utils.ConnectionLiveData
import com.avs.moviefinder.utils.RxBus
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.support.DaggerApplication
import javax.inject.Singleton


@Singleton
@Component(
    modules = [
        NetworkModule::class,
        ViewModelsModule::class,
        AndroidSupportInjectionModule::class,
        ActivityBindingModule::class,
        ContextModule::class,
        DatabaseModule::class,
        WorkerModule::class,
        BroadcastModule::class
    ]
)
interface AppComponent : AndroidInjector<DaggerApplication> {
    fun inject(application: MovieFinderApplication)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }

    fun serverApi(): ServerApi
    fun databaseManager(): DatabaseManager
    fun rxBus(): RxBus
    fun connectionLiveData(): ConnectionLiveData
}
