package com.avs.moviefinder.di.annotations

import androidx.work.ListenableWorker
import dagger.MapKey
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass

@MapKey
@Target(FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
internal annotation class WorkerKey(val value: KClass<out ListenableWorker>)