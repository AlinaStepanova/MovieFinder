package com.avs.moviefinder

import com.avs.moviefinder.di.AppComponent
import com.avs.moviefinder.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

open class MovieFinderApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val component: AppComponent = DaggerAppComponent.builder().application(this).build()
        component.inject(this)

        return component
    }
}
