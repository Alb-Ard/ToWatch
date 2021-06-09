package com.albard.towatch.recyclerviews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.albard.towatch.R;
import com.albard.towatch.database.Movie;

import java.util.ArrayList;
import java.util.List;

public class ChooseMovieRecyclerAdapter extends RecyclerView.Adapter<MovieHolder> {
    private final List<Movie> movies;
    private final MovieHolder.OnClickListener onClickListener;

    public ChooseMovieRecyclerAdapter(@Nullable final MovieHolder.OnClickListener onClickListener) {
        this.movies = new ArrayList<>();
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View holderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie,
            parent,
            false);
        return new MovieHolder(holderView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieHolder holder, final int position) {
        holder.bindMovie(this.movies.get(position));
        holder.setOnClickListener(this.onClickListener);
    }

    @Override
    public int getItemCount() {
        return this.movies.size();
    }

    public void setItems(@NonNull final List<Movie> movies) {
        this.movies.clear();
        this.movies.addAll(movies);
        this.notifyDataSetChanged();
    }
}
