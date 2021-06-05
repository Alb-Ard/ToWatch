package com.albard.towatch.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "movies")
public class Movie {
    @PrimaryKey
    private final int id;
    private final String name;

    public Movie(final int id, @NonNull final String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    @NonNull
    public String getName() {
        return this.name;
    }
}
