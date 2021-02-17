package com.avs.moviefinder.di

import com.avs.moviefinder.data.network.LocaleReceiver
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BroadcastModule {

    @ContributesAndroidInjector
    abstract fun contributesLocaleReceiver(): LocaleReceiver
}