package com.albard.towatch.recyclerviews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.albard.towatch.R;
import com.albard.towatch.database.Movie;

import java.util.ArrayList;
import java.util.List;

public class ChooseMovieRecyclerAdapter extends RecyclerView.Adapter<MovieHolder> implements Filterable {
    private final List<Movie> movies;
    private final MovieHolder.OnClickListener onClickListener;
    private final Filter filter;

    private List<Movie> moviesFiltered;

    public ChooseMovieRecyclerAdapter(@Nullable final MovieHolder.OnClickListener onClickListener) {
        this.movies = new ArrayList<>();
        this.moviesFiltered = new ArrayList<>();
        this.onClickListener = onClickListener;
        this.filter = new Filter() {
            @Override
            @NonNull
            protected FilterResults performFiltering(@Nullable final CharSequence constraint) {
                final FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    results.count = ChooseMovieRecyclerAdapter.this.movies.size();
                    results.values = ChooseMovieRecyclerAdapter.this.movies;
                    return results;
                }

                final CharSequence actualConstraint = constraint.toString().toUpperCase();
                final List<Movie> filterResult = new ArrayList<>();

                for (final Movie movie : ChooseMovieRecyclerAdapter.this.movies) {
                    if(movie.getName().toUpperCase().contains(actualConstraint)) {
                        filterResult.add(movie);
                    }
                }

                results.count = filterResult.size();
                results.values = filterResult;
                return results;
            }

            @Override
            protected void publishResults(@Nullable final CharSequence constraint, @NonNull final FilterResults results) {
                @SuppressWarnings("unchecked")
                final List<Movie> filterResult = (List<Movie>) results.values;
                ChooseMovieRecyclerAdapter.this.setMoviesFiltered(filterResult);
            }
        };
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
        holder.bindMovie(this.moviesFiltered.get(position));
        holder.setOnClickListener(this.onClickListener);
    }

    @Override
    public int getItemCount() {
        return this.moviesFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return this.filter;
    }

    public void setItems(@NonNull final List<Movie> movies) {
        this.movies.clear();
        this.movies.addAll(movies);
        this.setMoviesFiltered(movies);
    }

    protected void setMoviesFiltered(@NonNull final List<Movie> movies) {
        this.moviesFiltered = new ArrayList<>(movies);
        this.notifyDataSetChanged();
    }
}
