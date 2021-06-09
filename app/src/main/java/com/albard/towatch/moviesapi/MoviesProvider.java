package com.albard.towatch.moviesapi;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.albard.towatch.BuildConfig;
import com.albard.towatch.database.Movie;

import java.util.List;

public abstract class MoviesProvider {
    private static MoviesProvider SINGLETON;

    @Nullable
    public abstract List<Movie> request(@NonNull final String name, final boolean includeAdult);

    @NonNull
    public static MoviesProvider getSingleton() {
        if (MoviesProvider.SINGLETON == null) {
            MoviesProvider.SINGLETON = new TmdbMoviesProvider(BuildConfig.TMDB_API_KEY);
        }
        return MoviesProvider.SINGLETON;
    }
}
