package com.albard.towatch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.albard.towatch.utilities.ActionBarSetup;
import com.albard.towatch.utilities.Fragments;

public class HomeFragment extends Fragment {
    RandomTextView subtitleTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final FragmentActivity activity = this.getActivity();
        if (activity == null) {
            return;
        }

        ActionBarSetup.setBackButtonEnabled(activity, false);

        this.subtitleTextView = new RandomTextView(view.findViewById(R.id.homeFragment_subtitle_textView))
                .addValue(activity.getString(R.string.home_subtitle_1))
                .addValue(activity.getString(R.string.home_subtitle_2));
        this.subtitleTextView.extract("User");

        view.findViewById(R.id.homeFragment_addMovie_button).setOnClickListener(v ->
                Fragments.switchFragments(activity,
                        R.id.mainActivity_parent_containerView,
                        new EditMovieFragment(null),
                        Fragments.getTransactionName(this, "TO_EDIT"),
                        null)
        );
        view.findViewById(R.id.homeFragment_listMovies_button).setOnClickListener(v ->
                Fragments.switchFragments(activity,
                        R.id.mainActivity_parent_containerView,
                        new ListMoviesFragment(activity),
                        Fragments.getTransactionName(this, "TO_LIST"),
                        null)
        );
    }
}
