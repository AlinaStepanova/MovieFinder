package com.avs.moviefinder.di

import android.content.Context
import androidx.room.Room
import com.avs.moviefinder.data.database.MovieDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun getDatabase(context: Context): MovieDatabase {
        return Room.databaseBuilder(
            context,
            MovieDatabase::class.java,
            "movie_history_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}