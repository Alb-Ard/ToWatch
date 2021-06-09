package com.albard.towatch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.albard.towatch.database.MoviesRepository;
import com.albard.towatch.utilities.ActionBarSetup;
import com.albard.towatch.utilities.Fragments;

public class ListMoviesFragment extends ChooseMovieFragment {
    public ListMoviesFragment(@NonNull final FragmentActivity parentActivity) {
        super(new MoviesRepository()::getMovies, m -> Fragments.switchFragments(parentActivity,
                        R.id.mainActivity_parent_containerView,
                        new DetailsFragment(m),
                        Fragments.getTransactionName(ListMoviesFragment.class, "TO_DETAILS"),
                        null));
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_listmovies, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final FragmentActivity activity = this.getActivity();
        if (activity == null) {
            return;
        }

        ActionBarSetup.setBackButtonEnabled(activity, true);

        view.findViewById(R.id.listMoviesFragment_addMovie_fab).setOnClickListener(v ->
                Fragments.switchFragments(activity,
                        R.id.mainActivity_parent_containerView,
                        new EditMovieFragment(null),
                        Fragments.getTransactionName(this),
                        null)
        );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.requireActivity().getSupportFragmentManager().popBackStackImmediate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
