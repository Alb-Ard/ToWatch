package com.albard.towatch.database;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.albard.towatch.moviesapi.MovieImagesProvider;

import java.util.HashMap;
import java.util.Map;

public class MovieImagesRepository {
    private static final MovieImagesRepository SINGLETON = new MovieImagesRepository();

    private final Map<Movie, Bitmap> moviePosters = new HashMap<>();
    private final MovieImagesProvider provider;

    @NonNull
    public static MovieImagesRepository getSingleton() {
        return MovieImagesRepository.SINGLETON;
    }

    private MovieImagesRepository() {
        this.provider = MovieImagesProvider.createInstance();
    }

    @Nullable
    public Bitmap getMoviePoster(@NonNull final Movie movie) {
        if (movie.getImageUri() == null || movie.getImageUri().equals("")) {
            return null;
        }
        if (!this.moviePosters.containsKey(movie)) {
            this.moviePosters.put(movie, this.provider.request(movie));
        }
        return this.moviePosters.get(movie);
    }
}
