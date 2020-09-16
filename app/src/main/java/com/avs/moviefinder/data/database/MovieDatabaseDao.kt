package com.avs.moviefinder.data.database

import androidx.room.*
import com.avs.moviefinder.data.dto.Movie
import io.reactivex.Single
import java.util.*
import javax.inject.Singleton

@Singleton
@Dao
interface MovieDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(movie: Movie): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(movies: List<Movie>): Single<List<Long>>

    @Update
    fun update(movie: Movie): Single<Int>

    @Query("SELECT * from movie_table WHERE id = :id")
    fun get(id: Long): Single<Movie?>

    @Query("SELECT * from movie_table WHERE isFavorite = 1")
    fun getFavoritesList(): Single<List<Movie>?>

    @Query("SELECT * from movie_table WHERE isInWatchLater = 1")
    fun getWatchLaterList(): Single<List<Movie>?>

    @Query("SELECT * from movie_table")
    fun getAllEntries(): Single<List<Movie>>

    @Delete
    fun delete(movie: Movie)
}