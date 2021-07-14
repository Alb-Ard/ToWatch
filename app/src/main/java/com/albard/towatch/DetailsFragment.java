package com.albard.towatch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.albard.towatch.database.Movie;
import com.albard.towatch.database.MovieImagesRepository;
import com.albard.towatch.database.MoviesRepository;
import com.albard.towatch.utilities.ActionBarSetup;
import com.albard.towatch.utilities.Dialogs;
import com.albard.towatch.utilities.Fragments;
import com.albard.towatch.utilities.NetworkStatusHelper;
import com.albard.towatch.viewmodels.SelectedMovieViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DetailsFragment extends Fragment {
    private final MoviesRepository moviesRepository = new MoviesRepository();
    private SelectedMovieViewModel selectedMovieViewModel;
    private FloatingActionButton seenFab;
    private Movie movie;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull final Menu menu, @NonNull final MenuInflater inflater) {
        ActionBarSetup.setupMenu(menu, true, true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final FragmentActivity activity = this.getActivity();
        if (activity == null) {
            return;
        }

        ActionBarSetup.setBackButtonEnabled(activity, true);

        this.selectedMovieViewModel = new ViewModelProvider(activity).get(SelectedMovieViewModel.class);
        this.movie = this.selectedMovieViewModel.get();

        view.<TextView>findViewById(R.id.detailsFragment_movieName_textView).setText(this.movie.getName());
        view.<TextView>findViewById(R.id.detailsFragment_movieDescription_textView).setText(this.movie.getDescription());

        this.seenFab = view.findViewById(R.id.detailsFragment_setSeen_fab);
        this.seenFab.setOnClickListener(v -> {
            if (!this.movie.getSeen()) {
                SetMovieSeenActivity.start(activity, SetMovieSeenActivity.MASTER, this.movie.getId());
                return;
            }

            new AlertDialog.Builder(activity)
                    .setTitle(activity.getString(R.string.confirm))
                    .setMessage(R.string.reset_seen_state)
                    .setNegativeButton(activity.getString(R.string.cancel), (dialog, which) ->
                            dialog.dismiss()
                    )
                    .setPositiveButton(activity.getString(R.string.confirm), (dialog, which) -> {
                        this.movie.setSeen(false);
                        this.movie.setSeenWith("");
                        this.moviesRepository.insertMovie(this.movie);
                        dialog.dismiss();
                        this.backToList();
                    })
                    .show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        final View view = this.requireView();
        this.movie = this.moviesRepository.getMovie(this.movie.getId());
        this.selectedMovieViewModel.set(this.movie);

        final Bitmap image = MovieImagesRepository.getSingleton().getMoviePoster(this.movie, NetworkStatusHelper.getContextSingleton().isConnected());
        if (image == null) {
            view.<ImageView>findViewById(R.id.detailsFragment_moviePoster_imageView).setVisibility(View.GONE);
            view.<TextView>findViewById(R.id.detailsFragment_movieDescription_textView).setVisibility(View.VISIBLE);
        } else {
            view.<ImageView>findViewById(R.id.detailsFragment_moviePoster_imageView).setImageBitmap(image);
        }

        final boolean seen = this.movie.getSeen();
        if (seen) {
            view.<TextView>findViewById(R.id.detailsFragment_seen_textView).setText(String.format("%s:\n%s", getString(R.string.seen_with), this.movie.getSeenWith()));
        }
        this.seenFab.setImageDrawable(AppCompatResources.getDrawable(this.requireActivity(), seen ? R.drawable.ic_unseen_24 : R.drawable.ic_seen_24));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        final FragmentActivity activity = this.requireActivity();

        switch (item.getItemId()) {
            case R.id.detailsMenu_delete_menuItem:
                this.askDeleteConfirmation();
                return true;
            case R.id.detailsMenu_edit_menuItem:
                Fragments.switchFragments(activity,
                        R.id.mainActivity_parent_containerView,
                        new EditMovieFragment(),
                        Fragments.getTransactionName(this, "TO_EDIT"),
                        null);
                return true;
            case android.R.id.home:
                this.backToList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void backToList() {
        this.selectedMovieViewModel.set(null);
        this.requireActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    private void askDeleteConfirmation() {
        final FragmentActivity activity = this.requireActivity();
        final AlertDialog confirmDialog = new AlertDialog.Builder(activity).setTitle(activity.getString(R.string.confirm))
                .setMessage(activity.getString(R.string.confirm_delete))
                .setPositiveButton(activity.getString(R.string.confirm), (dialog, which) -> {
                    dialog.dismiss();
                    new MoviesRepository().removeMovie(this.movie.getId());
                    this.backToList();
                })
                .setNegativeButton(activity.getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                .create();
        confirmDialog.show();
    }
}
