package com.albard.towatch.moviesapi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.albard.towatch.BuildConfig;
import com.albard.towatch.database.Movie;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class MoviesProvider {
    private static MoviesProvider SINGLETON;

    private final ExecutorService executor;

    public MoviesProvider() {
        this.executor = Executors.newSingleThreadExecutor();
    }

    @Nullable
    public abstract List<Movie> findMovies(@NonNull final String name, final boolean includeAdult);

    public ExecutorService getExecutor() {
        return this.executor;
    }

    @NonNull
    public static MoviesProvider getSingleton() {
        if (MoviesProvider.SINGLETON == null) {
            MoviesProvider.SINGLETON = new TmdbMoviesProvider(BuildConfig.TMDB_API_KEY);
        }
        return MoviesProvider.SINGLETON;
    }
}
