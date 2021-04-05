package com.avs.moviefinder

import androidx.work.*
import com.avs.moviefinder.di.AppComponent
import com.avs.moviefinder.di.DaggerAppComponent
import com.avs.moviefinder.work.DeleteMoviesWorker
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import java.util.concurrent.TimeUnit

open class MovieFinderApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val component: AppComponent = DaggerAppComponent.builder().application(this).build()
        component.inject(this)

        return component
    }

    override fun onCreate() {
        super.onCreate()
        initDeleteMoviesWorker()
    }

    private fun initDeleteMoviesWorker() {
        val constrains = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(false)
            .build()

        val workRequest = PeriodicWorkRequest.Builder(DeleteMoviesWorker::class.java, 15, TimeUnit.MINUTES)
            .setConstraints(constrains)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            DeleteMoviesWorker.WORKER_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
