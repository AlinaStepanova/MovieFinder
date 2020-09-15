package com.avs.moviefinder.di

import android.app.Application
import com.avs.moviefinder.MovieFinderApplication
import com.avs.moviefinder.data.database.DatabaseManager
import com.avs.moviefinder.data.network.ServerApi
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
        ViewModelsModule::class,
        ContextModule::class,
        DatabaseModule::class
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

    fun serverApi() : ServerApi
    fun databaseManager() : DatabaseManager
    fun rxBus() : RxBus
}
