package com.albard.towatch.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "movies",
        indices = { @Index(value = { "tmdb_id" }, unique = true) })
public class Movie {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String description = "";
    private boolean seen = false;
    @ColumnInfo(name = "seen_with")
    private String seenWith = null;
    @ColumnInfo(name = "tmdb_id")
    private int dbId = 0;
    @ColumnInfo(name = "tmdb_image_uri")
    private String imageUri = null;

    public Movie(@NonNull final String name) {
        this.name = name;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    @NonNull
    public String getName() {
        return this.name;
    }

    public void setName(@NonNull final String name) {
        this.name = name;
    }

    public int getDbId() {
        return this.dbId;
    }

    public void setDbId(final int dbId) {
        this.dbId = dbId;
    }

    @Nullable
    public String getImageUri() {
        return this.imageUri;
    }

    public void setImageUri(@Nullable final String uri) {
        this.imageUri = uri;
    }

    @NonNull
    public String getDescription() {
        return this.description;
    }

    public void setDescription(@NonNull final String description) {
        this.description = description;
    }

    public boolean getSeen() {
        return this.seen;
    }

    public void setSeen(final boolean seen) {
        this.seen = seen;
    }

    @Nullable
    public String getSeenWith() {
        return this.seenWith;
    }

    public void setSeenWith(@Nullable final String seenWith) {
        this.seenWith = seenWith;
    }
}
