package com.avs.moviefinder.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.avs.moviefinder.data.dto.Movie

@Database(entities = [Movie::class], version = 1, exportSchema = false)
@TypeConverters(GenresConverter::class)
abstract class MovieDatabase : RoomDatabase() {

    abstract val movieDatabaseDao: MovieDatabaseDao

}