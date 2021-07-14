package com.albard.towatch;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.albard.towatch.database.Movie;
import com.albard.towatch.recyclerviews.ChooseMovieRecyclerAdapter;
import com.albard.towatch.utilities.ActionBarSetup;
import com.albard.towatch.utilities.Fragments;
import com.albard.towatch.viewmodels.SelectedMovieViewModel;

import java.util.List;

public abstract class ChooseMovieFragment extends Fragment {
    private final ListProvider moviesProvider;
    private final int titleResource;

    private SelectedMovieViewModel selectedMovieViewModel;
    private ChooseMovieRecyclerAdapter adapter;

    @FunctionalInterface
    public interface ListProvider {
        @NonNull
        List<Movie> get();
    }

    public ChooseMovieFragment(@NonNull final ListProvider moviesProvider,
                               @StringRes final int titleResource) {
        this.moviesProvider = moviesProvider;
        this.titleResource = titleResource;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
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
        ActionBarSetup.setTitle(activity, this.titleResource);

        this.selectedMovieViewModel = new ViewModelProvider(activity).get(SelectedMovieViewModel.class);
        final RecyclerView moviesRecyclerView = view.findViewById(R.id.chooseMovieFragment_moviesList_recyclerView);
        this.adapter = new ChooseMovieRecyclerAdapter(this::movieSelected);
        moviesRecyclerView.setAdapter(this.adapter);
        this.adapter.setItems(this.moviesProvider.get());

        final EditText searchText = view.findViewById(R.id.chooseMovieFragment_search_editText);
        searchText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(@Nullable final Editable s) {
                super.afterTextChanged(s);
                if (s == null) {
                    return;
                }
                ChooseMovieFragment.this.adapter.getFilter().filter(s.toString());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ActionBarSetup.setTitle(this.requireActivity(), R.string.app_name);
    }

    protected abstract void onMovieSelected(@Nullable final Movie movie);

    private void movieSelected(@Nullable final Movie movie) {
        this.selectedMovieViewModel.set(movie);
        this.onMovieSelected(movie);
    }
}
