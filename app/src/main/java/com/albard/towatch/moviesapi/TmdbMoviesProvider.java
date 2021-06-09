package com.albard.towatch.moviesapi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.albard.towatch.database.Movie;
import com.albard.towatch.utilities.NetworkStatusHelper;

import java.util.ArrayList;
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

    private final ExecutorService executor;
    private final TmdbApi api;

    public TmdbMoviesProvider(@NonNull final String apiKey) {
        this.executor = Executors.newSingleThreadExecutor();
        try {
            this.api = this.executor.submit(() -> new TmdbApi(apiKey)).get();
        } catch (final ExecutionException | InterruptedException e) {
            throw new RuntimeException("Couldn't initialize TMDb api", e);
        }
    }

    @Nullable
    @Override
    public List<Movie> request(@NonNull final String name, final boolean includeAdult) {
        if (!NetworkStatusHelper.getSingleton().isConnected()) {
            NetworkStatusHelper.getSingleton().showRequestDialog();
            return null;
        }

        try {
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
                final Artwork poster = this.executor.submit(() -> {
                    final MovieImages images = TmdbMoviesProvider.this.api.getMovies().getImages(dbMovie.getId(), null);
                    final List<Artwork> posters = images.getPosters();
                    if (posters == null || posters.size() == 0) {
                        return null;
                    }

                    return posters.get(0);
                }).get();
                if (poster != null) {
                    final String posterUri = this.api.getConfiguration().getBaseUrl() + TmdbMoviesProvider.IMAGE_URI_SIZE + poster.getFilePath();
                    movie.setImageUri(posterUri);
                }
                rv.add(movie);
            }
            return rv;
        } catch (final ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
