package com.avs.moviefinder

import android.app.Application
import com.avs.moviefinder.di.AppComponent
import com.avs.moviefinder.di.DaggerAppComponent

open class MovieFinderApplication : Application() {

    val appComponent: AppComponent by lazy {
        initializeComponent()
    }

    open fun initializeComponent(): AppComponent {
        return DaggerAppComponent.factory().create(applicationContext)
    }
}
