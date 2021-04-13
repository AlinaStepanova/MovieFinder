package com.avs.moviefinder.di.factories

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters

internal interface ChildWorkerFactory {

    fun create(appContext: Context, params: WorkerParameters): RxWorker
}