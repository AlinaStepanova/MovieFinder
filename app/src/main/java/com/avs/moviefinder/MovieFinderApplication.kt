package com.avs.moviefinder

import androidx.work.*
import com.avs.moviefinder.di.AppComponent
import com.avs.moviefinder.di.DaggerAppComponent
import com.avs.moviefinder.work.DeleteMoviesWorker
import com.avs.moviefinder.work.DeleteMoviesWorker.Companion.WORKER_NAME
import com.avs.moviefinder.work.DeleteMoviesWorker.Companion.WORKER_TAG
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import java.util.concurrent.TimeUnit
import javax.inject.Inject

open class MovieFinderApplication : DaggerApplication() {

    @Inject
    lateinit var workerFactory: WorkerFactory

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val component: AppComponent = DaggerAppComponent.builder().application(this).build()
        component.inject(this)

        return component
    }

    override fun onCreate() {
        super.onCreate()
        WorkManager.initialize(
            this,
            Configuration.Builder().setWorkerFactory(workerFactory).build()
        )
        initDeleteMoviesWorker()
    }

    private fun initDeleteMoviesWorker() {
        val constrains = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(false)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            WORKER_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<DeleteMoviesWorker>(12, TimeUnit.HOURS)
                .addTag(WORKER_TAG)
                .setConstraints(constrains)
                .build()
        )
    }
}
