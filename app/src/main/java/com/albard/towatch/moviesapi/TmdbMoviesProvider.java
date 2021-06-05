package com.albard.towatch.moviesapi;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

public class TmdbMoviesProvider implements MoviesProvider {
    private static final int CACHED_TIME_MILLIS = 120 * 1000;

    private final TmdbApi api;

    private Date cachedMoviesExpireDate;
    private TmdbSearch cachedMovies;

    public TmdbMoviesProvider(@NonNull final String apiKey)
    {
        this.api = new TmdbApi(apiKey);
    }

    @NonNull
    @Override
    public List<MovieReply> request(@NonNull final String name, final boolean includeAdult) {
        final TmdbSearch search = this.getSearch();
        final MovieResultsPage results = search.searchMovie(name,
                0,
                null,
                includeAdult,
                0);
        final List<MovieDb> movies = results.getResults();
        final List<MovieReply> rv = new ArrayList<>();
        for (MovieDb movie : movies) {
            rv.add(new MovieReply(movie.getTitle()));
        }
        return rv;
    }

    private TmdbSearch getSearch() {
        if (this.cachedMovies != null && new Date().after(this.cachedMoviesExpireDate)) {
            return this.cachedMovies;
        }
        this.cachedMovies = this.api.getSearch();
        this.cachedMoviesExpireDate = new Date(System.currentTimeMillis() + TmdbMoviesProvider.CACHED_TIME_MILLIS);
        return this.cachedMovies;
    }
}
