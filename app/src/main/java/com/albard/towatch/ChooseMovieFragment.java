package com.albard.towatch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.albard.towatch.database.Movie;
import com.albard.towatch.recyclerviews.ChooseMovieRecyclerAdapter;
import com.albard.towatch.utilities.ActionBarSetup;

import java.util.List;

public class ChooseMovieFragment extends Fragment {
    private final OnMovieChosen onMovieChosen;
    private final ListProvider moviesProvider;

    @FunctionalInterface
    public interface ListProvider {
        @NonNull
        List<Movie> get();
    }

    @FunctionalInterface
    public interface OnMovieChosen {
        void execute(@Nullable final Movie movie);
    }

    public ChooseMovieFragment(@NonNull final ListProvider moviesProvider, @Nullable final OnMovieChosen onMovieChosen) {
        this.moviesProvider = moviesProvider;
        this.onMovieChosen = onMovieChosen;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choosemovie, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final FragmentActivity activity = this.getActivity();
        if (activity == null) {
            return;
        }

        ActionBarSetup.setBackButtonEnabled(activity, false);

        final RecyclerView moviesRecyclerView = view.findViewById(R.id.chooseMovieFragment_moviesList_recyclerView);
        final ChooseMovieRecyclerAdapter adapter = new ChooseMovieRecyclerAdapter(this.onMovieChosen != null ? this.onMovieChosen::execute : null);
        moviesRecyclerView.setAdapter(adapter);
        adapter.setItems(this.moviesProvider.get());
    }
}
