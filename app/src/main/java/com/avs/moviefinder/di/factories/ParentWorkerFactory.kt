package com.avs.moviefinder.di.factories

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject
import javax.inject.Provider

internal class ParentWorkerFactory @Inject constructor(
    private val workerFactory: Map<Class<out ListenableWorker>, @JvmSuppressWildcards Provider<ChildWorkerFactory>>
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {

        val factoryEntry =
            workerFactory.entries.find { Class.forName(workerClassName).isAssignableFrom(it.key) }

        return if (factoryEntry != null) {
            val factoryProvider = factoryEntry.value
            factoryProvider.get().create(appContext, workerParameters)
        } else {
            val workerClass =
                Class.forName(workerClassName).asSubclass(ListenableWorker::class.java)
            val constructor = workerClass.getDeclaredConstructor(
                Context::class.java,
                WorkerParameters::class.java
            )
            constructor.newInstance(appContext, workerParameters)
        }
    }
}