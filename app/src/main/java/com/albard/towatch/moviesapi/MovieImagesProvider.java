package com.albard.towatch.moviesapi;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.albard.towatch.database.Movie;

public abstract class MovieImagesProvider {
    @Nullable
    public abstract Bitmap request(@NonNull final Movie movie);

    @NonNull
    public static MovieImagesProvider createInstance() {
        return new TmdbMovieImagesProvider();
    }
}
