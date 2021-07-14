package com.albard.towatch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.albard.towatch.database.Movie;
import com.albard.towatch.database.MovieImagesRepository;
import com.albard.towatch.database.MoviesRepository;
import com.albard.towatch.recyclerviews.SetSeenUsersRecyclerAdapter;
import com.albard.towatch.seenlobby.LobbyClient;
import com.albard.towatch.seenlobby.LobbyCommonNetwork;
import com.albard.towatch.seenlobby.LobbyServer;
import com.albard.towatch.seenlobby.OnUserReceived;
import com.albard.towatch.seenlobby.UsersViewModel;
import com.albard.towatch.settings.UserSettings;
import com.albard.towatch.utilities.ActionBarSetup;

import java.util.ArrayList;
import java.util.Objects;

public class SetSeenExternalFragment extends Fragment implements LobbyCommonNetwork.OnMovieReceived {
    private final String ip;
    private final int port;

    private Movie movie;
    private LobbyClient client;

    private UsersViewModel usersViewModel;
    private SetSeenUsersRecyclerAdapter usersRecyclerAdapter;

    public SetSeenExternalFragment(@NonNull final String ip, final int port) {
        this.ip = ip;
        this.port = port;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setseen_external, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        final FragmentActivity activity = this.requireActivity();

        // Setup client
        this.client = new LobbyClient(new UserSettings(this.requireContext()).getUserName());

        final RecyclerView usersRecyclerView = this.requireView().findViewById(R.id.setSeenUsersFragment_list_recyclerView);
        this.usersRecyclerAdapter = new SetSeenUsersRecyclerAdapter();
        usersRecyclerView.setAdapter(this.usersRecyclerAdapter);

        this.usersViewModel = new ViewModelProvider(activity).get(UsersViewModel.class);
        this.usersViewModel.setLocalUserPostfix(activity.getString(R.string.local_user_postfix));
        this.usersViewModel.getUsers().observe(this.getViewLifecycleOwner(), users -> {
            this.usersViewModel.setLocalUserIndex(this.client.getSharedId());
            this.usersRecyclerAdapter.setUsers(new ArrayList<>(Objects.requireNonNull(this.usersViewModel.getUsers().getValue()).values()));
        });

        this.client.setOnMovieReceived(this);
        this.client.setOnDisconnect(this::endRoom);
        this.client.addOnUserReceived(this.usersViewModel);

        // Tries to start the client
        final boolean clientStarted = this.client.connect(this.ip, this.port);
        if (!clientStarted) {
            Toast.makeText(this.requireActivity(), "Error starting client", Toast.LENGTH_SHORT).show();
            activity.finish();
        }
    }

    @Override
    public void onStop() {
        if (this.client != null) {
            this.client.close();
        }
        super.onStop();
    }

    @Override
    public void receive(@NonNull final Movie movie) {
        final View view = this.requireView();
        this.movie = movie;
        view.<TextView>findViewById(R.id.setSeenExternalFragment_movieName_textView)
                .setText(this.movie.getName());
        if (movie.getImageUri() != null) {
            view.<ImageView>findViewById(R.id.setSeenExternalFragment_moviePoster_imageView).setImageBitmap(MovieImagesRepository.getSingleton().getMoviePoster(movie, true));
        } else {
            view.<TextView>findViewById(R.id.setSeenExternalFragment_noPosterText_textView).setVisibility(View.VISIBLE);
            view.<ImageView>findViewById(R.id.setSeenExternalFragment_moviePoster_imageView).setVisibility(View.GONE);
        }
    }

    private void endRoom(final boolean completed) {
        Log.d(getClass().getName(), "Lobby ended (completed=" + completed + ")");
        if (completed) {
            this.movie.setSeen(true);
            this.movie.setSeenWith(this.usersViewModel.toString());
            new MoviesRepository().insertMovie(this.movie);
        }
        this.requireActivity().finish();
    }
}
