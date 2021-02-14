package com.avs.moviefinder.di

import com.avs.moviefinder.ui.search_result.SearchResultFragment
import com.avs.moviefinder.ui.home.HomeFragment
import com.avs.moviefinder.ui.watch_later.WatchLaterFragment
import com.avs.moviefinder.ui.favorites.FavoritesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBindingModule {

    @ContributesAndroidInjector
    abstract fun provideHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun provideFindDetailFragment(): SearchResultFragment

    @ContributesAndroidInjector
    abstract fun provideWatchLaterFragment(): WatchLaterFragment

    @ContributesAndroidInjector
    abstract fun provideFavoritesFragment(): FavoritesFragment
}