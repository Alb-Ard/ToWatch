package com.albard.towatch.database;

import android.app.Activity;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.albard.towatch.OnActionHandler;
import com.albard.towatch.moviesapi.MovieImagesProvider;
import com.albard.towatch.utilities.Dialogs;
import com.albard.towatch.utilities.NetworkStatusHelper;

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
    public Bitmap getMoviePoster(@NonNull final Movie movie, final boolean canUseNetwork) {
        if (movie.getImageUri() == null || movie.getImageUri().equals("")) {
            return null;
        }
        if (!this.moviePosters.containsKey(movie) || this.moviePosters.get(movie) == null) {
            final Bitmap image = canUseNetwork ? this.provider.request(movie) : null;
            this.moviePosters.put(movie, image);
        }
        return this.moviePosters.get(movie);
    }
}
