package com.albard.towatch.moviesapi;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.albard.towatch.database.Movie;
import com.albard.towatch.utilities.Dialogs;
import com.albard.towatch.utilities.NetworkStatusHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.Artwork;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.MovieImages;

public class TmdbMoviesProvider extends MoviesProvider {
    private static final String IMAGE_URI_SIZE = "w500";
    private static final String DEFAULT_LANGUAGE = "en";

    private final ExecutorService executor;

    private TmdbApi api;

    public TmdbMoviesProvider(@NonNull final String apiKey) {
        this.executor = Executors.newSingleThreadExecutor();
        try {
            this.api = this.executor.submit(() -> new TmdbApi(apiKey)).get();
        } catch (final ExecutionException | InterruptedException e) {
            Log.w(getClass().getName(), "Couldn't initialize TMDb api: " + e.getMessage());
        }
    }

    @Nullable
    @Override
    public List<Movie> findMovies(@NonNull final String name, final boolean includeAdult) {
        try {
            if (this.api == null) {
                return new ArrayList<>();
            }

            final List<MovieDb> movies = this.executor.submit(() -> TmdbMoviesProvider.this.api.getSearch().searchMovie(name,
                    0,
                    null,
                    includeAdult,
                    0).getResults())
                    .get();
            final List<Movie> rv = new ArrayList<>();
            for (final MovieDb dbMovie : movies) {
                final Movie movie = new Movie(dbMovie.getTitle());
                movie.setDescription(dbMovie.getOverview());
                movie.setDbId(dbMovie.getId());
                final Artwork poster = this.executor.submit(() -> this.getPoster(dbMovie)).get();
                if (poster != null) {
                    final String posterUri = this.api.getConfiguration().getBaseUrl() + TmdbMoviesProvider.IMAGE_URI_SIZE + poster.getFilePath();
                    movie.setImageUri(posterUri);
                }
                rv.add(movie);
            }
            return rv;
        } catch (final ExecutionException | InterruptedException e) {
            Log.w(getClass().getName(), Arrays.toString(e.getStackTrace()));
            return new ArrayList<>();
        }
    }

    private Artwork getPoster(@NonNull final MovieDb movie) {
        Artwork result = this.getPosterInLanguage(movie, TmdbMoviesProvider.DEFAULT_LANGUAGE);
        if (result != null) {
            return result;
        }
        result = this.getPosterInLanguage(movie, movie.getOriginalLanguage());
        if (result != null) {
            return result;
        }
        return this.getPosterInLanguage(movie, null);
    }

    private Artwork getPosterInLanguage(@NonNull final MovieDb movie, @Nullable final String language) {
        if (this.api == null) {
            return null;
        }

        MovieImages images = TmdbMoviesProvider.this.api.getMovies().getImages(movie.getId(), movie.getOriginalLanguage());
        List<Artwork> posters = images.getPosters();
        return posters != null && posters.size() > 0 ? posters.get(0) : null;
    }
}
