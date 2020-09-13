package com.avs.moviefinder.data.database

import androidx.room.*
import com.avs.moviefinder.data.dto.Movie

@Dao
interface MovieDatabaseDao {
    @Insert
    fun insert(movie: Movie)

    @Update
    fun update(movie: Movie)

    @Query("SELECT * from movie_table WHERE id = :id")
    fun get(id: Long): Movie?

    @Query("SELECT * from movie_table WHERE isFavorite = 1")
    fun getFavoritesList(): List<Movie>?

    @Query("SELECT * from movie_table WHERE isInWatchLater = 1")
    fun getWatchLaterList(): List<Movie>?

    @Delete
    fun delete(movie: Movie)
}