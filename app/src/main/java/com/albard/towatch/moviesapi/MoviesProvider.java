package com.albard.towatch.moviesapi;

import androidx.annotation.NonNull;

import java.util.List;

public interface MoviesProvider {
    @NonNull
    List<MovieReply> request(@NonNull final String name, final boolean includeAdult);
}
