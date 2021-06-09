package com.albard.towatch.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(version = 1, entities = { Movie.class })
public abstract class MoviesDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "movies_db";
    private static final int THREADS_COUNT = 2;

    private static MoviesDatabase SINGLETON;

    private final ExecutorService executor =
            Executors.newFixedThreadPool(MoviesDatabase.THREADS_COUNT);

    @NonNull
    public static MoviesDatabase initializeSingleton(@NonNull final Context context) {
        synchronized (MoviesDatabase.class) {
            if (MoviesDatabase.isInitialized()) {
                return MoviesDatabase.SINGLETON;
            }
            MoviesDatabase.SINGLETON = Room.databaseBuilder(context.getApplicationContext(),
                    MoviesDatabase.class,
                    MoviesDatabase.DATABASE_NAME)
                    .build();
        }
        return MoviesDatabase.SINGLETON;
    }

    @NonNull
    public static MoviesDatabase getSingleton() {
        if (!MoviesDatabase.isInitialized()) {
            throw new RuntimeException("Database singleton not initialized");
        }
        return MoviesDatabase.SINGLETON;
    }

    public static boolean isInitialized() {
        return MoviesDatabase.SINGLETON != null;
    }

    @NonNull
    public abstract MoviesDao getMoviesDao();

    @NonNull
    public ExecutorService getExecutor() {
        return this.executor;
    }
}
