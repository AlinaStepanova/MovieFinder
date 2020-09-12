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

    @Delete
    fun delete(movie: Movie)
}