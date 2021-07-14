package com.albard.towatch.database;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.albard.towatch.OnActionHandler;
import com.albard.towatch.moviesapi.MoviesProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class MoviesRepository {
    private final MoviesProvider provider;
    private final MoviesDao moviesDao;
    private final MoviesDatabase database;
    private final ExecutorService databaseExecutor;
    private final ExecutorService providerExecutor;

    public MoviesRepository() {
        if (!MoviesDatabase.isInitialized()) {
            throw new RuntimeException("Database must be initialized first");
        }

        this.provider = MoviesProvider.getSingleton();
        this.providerExecutor = this.provider.getExecutor();

        this.database = MoviesDatabase.getSingleton();
        this.databaseExecutor = this.database.getExecutor();
        this.moviesDao = this.database.getMoviesDao();
    }

    public void insertMovie(@NonNull final Movie movie) {
        try {
            this.databaseExecutor.submit(() -> this.moviesDao.insertMovie(movie)).get();
        } catch (final ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    public List<Movie> getMovies() {
        try {
            return this.databaseExecutor.submit(this.moviesDao::getMovies).get();
        } catch (final ExecutionException | InterruptedException e) {
            throw new RuntimeException("Exception executing query", e);
        }
    }

    @NonNull
    public Movie getMovie(final int id) {
        try {
            return this.databaseExecutor.submit(() -> this.moviesDao.getMovie(id)).get();
        } catch (final ExecutionException | InterruptedException e) {
            throw new RuntimeException("Exception executing query", e);
        }
    }

    @NonNull
    public LiveData<List<Movie>> findMovies(@NonNull final String name, final boolean includeAdult) {
        final MutableLiveData<List<Movie>> result = new MutableLiveData<>();
        this.providerExecutor.execute(() ->
                result.postValue(this.provider.findMovies(name, includeAdult))
        );
        return result;
    }

    public void removeMovie(final int id) {
        this.databaseExecutor.execute(() -> this.moviesDao.removeMovie(id));
    }
}
