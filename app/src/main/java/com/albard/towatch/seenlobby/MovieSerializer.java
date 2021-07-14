package com.albard.towatch.seenlobby;

import androidx.annotation.NonNull;

import com.albard.towatch.database.Movie;

public class MovieSerializer {
    private static final String SEPARATOR = "\0";

    @NonNull
    public String serialize(@NonNull final Movie movie) {
        final StringBuilder builder = new StringBuilder().append(movie.getName())
                .append(MovieSerializer.SEPARATOR)
                .append(movie.getDescription());
        if (movie.getDbId() != 0) {
            builder.append(MovieSerializer.SEPARATOR)
                    .append(movie.getDbId())
                    .append(MovieSerializer.SEPARATOR)
                    .append(movie.getImageUri());
        }
        return builder.toString();
    }

    public Movie deserialize(@NonNull final String from) {
        final String[] parts = from.split(MovieSerializer.SEPARATOR);

        final Movie movie = new Movie(parts[0]);
        movie.setDescription(parts[1]);
        if (parts.length > 2) {
            movie.setDbId(Integer.parseInt(parts[2]));
            movie.setImageUri(parts[3]);
        }

        return movie;
    }
}
