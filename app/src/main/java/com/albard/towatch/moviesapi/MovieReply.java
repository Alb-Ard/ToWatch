package com.albard.towatch.moviesapi;

import androidx.annotation.NonNull;

public class MovieReply {
    private final String name;

    public MovieReply(@NonNull final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
