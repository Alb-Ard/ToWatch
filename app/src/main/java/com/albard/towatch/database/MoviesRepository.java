package com.albard.towatch.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.albard.towatch.moviesapi.MoviesProvider;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import info.movito.themoviedbapi.model.Artwork;

public class MoviesRepository {
    private final MoviesProvider provider;
    private final MoviesDao moviesDao;
    private final MoviesDatabase database;
    private final ExecutorService executor;

    public MoviesRepository() {
        if (!MoviesDatabase.isInitialized()) {
            throw new RuntimeException("Database must be initialized first");
        }

        this.provider = MoviesProvider.getSingleton();
        this.database = MoviesDatabase.getSingleton();
        this.executor = this.database.getExecutor();
        this.moviesDao = this.database.getMoviesDao();
    }

    public void insertMovie(@NonNull final Movie movie) {
        this.executor.execute(() -> this.moviesDao.insertMovie(movie));
    }

    @NonNull
    public List<Movie> getMovies() {
        try {
            return this.executor.submit(this.moviesDao::getMovies).get();
        } catch (final ExecutionException | InterruptedException e) {
            throw new RuntimeException("Exception executing query", e);
        }
    }

    @NonNull
    public Movie getMovie(final int id) {
        try {
            return this.executor.submit(() -> this.moviesDao.getMovie(id)).get();
        } catch (final ExecutionException | InterruptedException e) {
            throw new RuntimeException("Exception executing query", e);
        }
    }

    @NonNull
    public List<Movie> findMovies(@NonNull final String name, final boolean includeAdult) {
        return this.provider.request(name, includeAdult);
    }

    public void removeMovie(final int id) {
        this.executor.execute(() -> this.moviesDao.removeMovie(id));
    }
}
