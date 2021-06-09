package com.albard.towatch;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.albard.towatch.database.Movie;
import com.albard.towatch.database.MoviesRepository;
import com.albard.towatch.settings.UserSettings;
import com.albard.towatch.utilities.ActionBarSetup;
import com.albard.towatch.utilities.Fragments;
import com.albard.towatch.utilities.NetworkStatusHelper;

import java.util.List;

public class EditMovieFragment extends Fragment {
    private EditText movieNameEditText;
    private EditText movieDescriptionEditText;

    private MoviesRepository repository;
    private Movie currentMovie;
    private UserSettings userSettings;

    public EditMovieFragment(@Nullable final Movie movie) {
        this.currentMovie = movie == null ? new Movie("") : movie;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_editmovie, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final FragmentActivity activity = this.getActivity();
        if (activity == null) {
            return;
        }

        ActionBarSetup.setBackButtonEnabled(activity, true);

        if (this.currentMovie == null) {
            Log.e(EditMovieFragment.class.getName(), "No movie specified");
            activity.getSupportFragmentManager().popBackStackImmediate();
        }

        this.userSettings = new UserSettings(activity);
        this.repository = new MoviesRepository();

        this.movieNameEditText = view.findViewById(R.id.editMovie_movieName_editText);
        this.movieDescriptionEditText = view.findViewById(R.id.editMovie_movieDescription_editText);
        view.findViewById(R.id.editMovie_searchInfo_imageButton).setOnClickListener(v ->
            this.searchMovie(this.movieNameEditText.getText().toString())
        );
        view.findViewById(R.id.editMovie_apply_imageButton).setOnClickListener(v -> this.apply());

        this.setMovieInfo(this.currentMovie);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.requireActivity().getSupportFragmentManager().popBackStackImmediate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchMovie(@NonNull final String name) {
        final FragmentActivity activity = this.requireActivity();
        if (!NetworkStatusHelper.getSingleton().isConnected()) {
            NetworkStatusHelper.getSingleton().showRequestDialog();
            return;
        }

        final List<Movie> movies = this.repository.findMovies(name, !this.userSettings.getAdultFilter());
        if (movies.size() == 0) {
            Toast.makeText(activity, activity.getString(R.string.no_results_found), Toast.LENGTH_LONG).show();
            return;
        }

        Fragments.switchFragments(activity,
                R.id.mainActivity_parent_containerView,
                new ChooseMovieFragment(() -> movies, m ->
                    Fragments.switchFragments(activity,
                            R.id.mainActivity_parent_containerView,
                            new EditMovieFragment(m))
                ));
    }

    private void apply() {
        this.currentMovie.setName(this.movieNameEditText.getText().toString());
        this.repository.insertMovie(this.currentMovie);
        this.requireActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    private void setMovieInfo(@NonNull final Movie movie) {
        this.currentMovie = movie;
        this.movieNameEditText.setText(movie.getName());
        this.movieDescriptionEditText.setText(movie.getDescription());
    }
}
