package com.avs.moviefinder.utils

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * An RxJava event bus implementation.
 */
@Singleton
class RxBus @Inject constructor() {

    private val relay = PublishRelay.create<Any>()
    fun send(event: Any) {
        relay.accept(event)
    }

    val events: Observable<Any>
        get() = relay.observeOn(AndroidSchedulers.mainThread())
}