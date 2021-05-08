package com.avs.moviefinder.di.modules

import com.avs.moviefinder.di.annotations.PerFragment
import com.avs.moviefinder.ui.favorites.FavoritesFragment
import com.avs.moviefinder.ui.home.HomeFragment
import com.avs.moviefinder.ui.search_result.SearchResultFragment
import com.avs.moviefinder.ui.watch_later.WatchLaterFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBindingModule {

    @PerFragment
    @ContributesAndroidInjector
    abstract fun provideHomeFragment(): HomeFragment

    @PerFragment
    @ContributesAndroidInjector
    abstract fun provideFindDetailFragment(): SearchResultFragment

    @PerFragment
    @ContributesAndroidInjector
    abstract fun provideWatchLaterFragment(): WatchLaterFragment

    @PerFragment
    @ContributesAndroidInjector
    abstract fun provideFavoritesFragment(): FavoritesFragment
}