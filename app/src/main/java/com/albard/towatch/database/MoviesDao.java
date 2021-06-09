package com.albard.towatch.database;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MoviesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(@NonNull final Movie movie);

    @Query("SELECT * FROM movies")
    List<Movie> getMovies();

    @Query("SELECT * FROM movies WHERE movies.id = :id")
    Movie getMovie(final int id);

    @Delete
    void removeMovie(@NonNull final Movie movie);

    @Query("DELETE FROM movies WHERE movies.id = :id")
    void removeMovie(final int id);
}
