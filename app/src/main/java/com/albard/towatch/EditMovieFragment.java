package com.albard.towatch;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.albard.towatch.database.Movie;
import com.albard.towatch.database.MoviesRepository;
import com.albard.towatch.settings.UserSettings;
import com.albard.towatch.utilities.ActionBarSetup;
import com.albard.towatch.utilities.Dialogs;
import com.albard.towatch.utilities.Fragments;
import com.albard.towatch.utilities.NetworkStatusHelper;
import com.albard.towatch.utilities.Utils;
import com.albard.towatch.viewmodels.SelectedMovieViewModel;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class EditMovieFragment extends Fragment {
    private TextInputLayout movieNameTextLayout;
    private EditText movieNameEditText;
    private EditText movieDescriptionEditText;

    private MoviesRepository repository;
    private Movie currentMovie;
    private UserSettings userSettings;

    public static class QueryResultFragment extends ChooseMovieFragment {
        public QueryResultFragment(@NonNull final ListProvider moviesProvider, final int titleResource) {
            super(moviesProvider, titleResource);
        }

        @Override
        protected void onMovieSelected(@Nullable final Movie movie) {
            this.requireActivity().getSupportFragmentManager().popBackStackImmediate();
        }
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

        this.userSettings = new UserSettings(activity);
        this.repository = new MoviesRepository();

        this.movieNameTextLayout = view.findViewById(R.id.editMovieFragment_movieName_textLayout);
        this.movieNameTextLayout.setError(this.getString(R.string.invalid_name));
        this.setNameError(false);
        this.movieNameEditText = view.findViewById(R.id.editMovieFragment_movieName_editText);
        this.movieDescriptionEditText = view.findViewById(R.id.editMovieFragment_movieDescription_editText);
        view.findViewById(R.id.editMovieFragment_searchInfo_imageButton).setOnClickListener(v -> {
            final String name = this.movieNameEditText.getText().toString();
            if (name == null || name.length() == 0) {
                this.setNameError(true);
                return;
            }
            this.setNameError(false);
            this.searchMovie(this.movieNameEditText.getText().toString());
        });
        view.findViewById(R.id.editMovieFragment_confirm_fab).setOnClickListener(v -> this.apply());
    }

    @Override
    public void onResume() {
        super.onResume();

        final SelectedMovieViewModel selectedMovieViewModel = new ViewModelProvider(this.requireActivity()).get(SelectedMovieViewModel.class);
        final Movie selectMovie = selectedMovieViewModel.get();
        selectedMovieViewModel.set(null);
        this.setMovieInfo(selectMovie != null ? selectMovie : new Movie(""));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Utils.hideKeyboard(this.requireActivity());
            this.requireActivity().getSupportFragmentManager().popBackStackImmediate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchMovie(@NonNull final String name) {
        final FragmentActivity activity = this.requireActivity();
        if (!NetworkStatusHelper.getContextSingleton().isConnected()) {
            NetworkStatusHelper.getContextSingleton().executeIfNotConnected(Dialogs::showNotConnected, activity);
            return;
        }

        final AlertDialog progressDialog = new AlertDialog.Builder(activity)
                .setMessage(this.getString(R.string.searching))
                .setCancelable(false)
                .create();
        progressDialog.show();

        final LiveData<List<Movie>> moviesResult = this.repository.findMovies(name, !this.userSettings.getAdultFilter());

        final View viewInFocus = activity.getCurrentFocus();
        if (viewInFocus != null) {
            viewInFocus.clearFocus();
        }

        moviesResult.observe(this.getViewLifecycleOwner(), movies -> {
            progressDialog.dismiss();

            if (movies.size() == 0) {
                Toast.makeText(activity, activity.getString(R.string.no_results_found), Toast.LENGTH_LONG).show();
                return;
            }

            Fragments.switchFragments(activity,
                    R.id.mainActivity_parent_containerView,
                    new QueryResultFragment(() -> movies, R.string.select_movie),
                    Fragments.getTransactionName(this, "TO_QUERY"),
                    null
            );
        });
    }

    private void apply() {
        final String name = this.movieNameEditText.getText().toString();
        if (name == null || name.length() == 0) {
            this.setNameError(true);
            return;
        }
        this.setNameError(false);

        this.currentMovie.setName(name);
        this.repository.insertMovie(this.currentMovie);
        this.requireActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    private void setNameError(final boolean enabled) {
        this.movieNameTextLayout.setErrorEnabled(enabled);
    }

    private void setMovieInfo(@NonNull final Movie movie) {
        this.currentMovie = movie;
        this.movieNameEditText.setText(movie.getName());
        this.movieDescriptionEditText.setText(movie.getDescription());
    }
}
