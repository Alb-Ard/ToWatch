package com.albard.towatch;

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
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.albard.towatch.database.Movie;
import com.albard.towatch.database.MovieImagesRepository;
import com.albard.towatch.database.MoviesRepository;
import com.albard.towatch.utilities.ActionBarSetup;
import com.albard.towatch.utilities.Fragments;

import java.util.Objects;

public class DetailsFragment extends Fragment {
    private final Movie movie;

    public DetailsFragment(@NonNull final Movie movie) {
        this.movie = movie;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull final Menu menu, @NonNull final MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.details, menu);
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

        view.<TextView>findViewById(R.id.detailsFragment_movieName_textView).setText(this.movie.getName());
        view.<TextView>findViewById(R.id.detailsFragment_movieDescription_textView).setText(this.movie.getDescription());

        final Bitmap image = MovieImagesRepository.getSingleton().getMoviePoster(this.movie);
        if (image == null) {
            view.<ImageView>findViewById(R.id.detailsFragment_moviePoster_imageView).setVisibility(View.GONE);
            view.<TextView>findViewById(R.id.detailsFragment_movieDescription_textView).setVisibility(View.VISIBLE);
            return;
        }
        view.<ImageView>findViewById(R.id.detailsFragment_moviePoster_imageView).setImageBitmap(image);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.detailsMenu_delete_menuItem:
                this.askDeleteConfirmation();
                return true;
            case R.id.detailsMenu_edit_menuItem:
                Fragments.switchFragments(this.requireActivity(),
                        R.id.mainActivity_parent_containerView,
                        new EditMovieFragment(this.movie),
                        Fragments.getTransactionName(this, "TO_EDIT"),
                        null);
                return true;
            case R.id.detailsMenu_seen_menuItem:
                return true;
            case android.R.id.home:
                this.requireActivity().getSupportFragmentManager().popBackStackImmediate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void askDeleteConfirmation() {
        final FragmentActivity activity = this.requireActivity();
        final AlertDialog confirmDialog = new AlertDialog.Builder(activity).setTitle("Confirm")
                .setMessage("Are you sure you want to delete this movie?")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    dialog.dismiss();
                    new MoviesRepository().removeMovie(this.movie.getId());
                    activity.getSupportFragmentManager().popBackStackImmediate();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create();
        confirmDialog.show();
    }
}
