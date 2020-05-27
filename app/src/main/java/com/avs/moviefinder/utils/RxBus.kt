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
    fun send(o: Any) {
        relay.accept(o)
    }

    val events: Observable<Any>
        get() = relay.observeOn(AndroidSchedulers.mainThread())
}