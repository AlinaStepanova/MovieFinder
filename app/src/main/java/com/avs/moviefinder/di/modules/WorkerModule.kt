package com.avs.moviefinder.di.modules

import androidx.work.WorkerFactory
import com.avs.moviefinder.di.annotations.WorkerKey
import com.avs.moviefinder.di.factories.ChildWorkerFactory
import com.avs.moviefinder.di.factories.ParentWorkerFactory
import com.avs.moviefinder.work.DeleteMoviesWorker
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class WorkerModule {

    @Binds
    abstract fun bindParentWorkerFactory(factory: ParentWorkerFactory): WorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(DeleteMoviesWorker::class)
    abstract fun bindDeleteMoviesWorker(worker: DeleteMoviesWorker.Factory): ChildWorkerFactory
}