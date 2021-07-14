package com.albard.towatch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.albard.towatch.database.MoviesRepository;
import com.albard.towatch.recyclerviews.SetSeenUsersRecyclerAdapter;
import com.albard.towatch.seenlobby.LobbyCommonNetwork;
import com.albard.towatch.seenlobby.LobbyServer;
import com.albard.towatch.seenlobby.UsersViewModel;
import com.albard.towatch.settings.UserSettings;
import com.albard.towatch.utilities.ActionBarSetup;
import com.albard.towatch.utilities.Dialogs;
import com.albard.towatch.utilities.MovieQrGenerator;
import com.albard.towatch.utilities.NetworkStatusHelper;

import java.util.ArrayList;
import java.util.Objects;

public class SetSeenMasterFragment extends Fragment {
    private final Movie movie;

    private UsersViewModel usersViewModel;
    private LobbyServer server;

    private SetSeenUsersRecyclerAdapter usersRecyclerAdapter;

    public SetSeenMasterFragment(@NonNull final Movie movie) {
        this.movie = movie;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setseen_master, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final FragmentActivity activity = this.getActivity();
        if (activity == null) {
            return;
        }

        ActionBarSetup.setBackButtonEnabled(activity, true);

        // Always active components
        view.<TextView>findViewById(R.id.setSeenMasterFragment_movieName_textView)
                .setText(this.movie.getName());
        view.findViewById(R.id.setSeenMasterFragment_confirm_fab).setOnClickListener(v ->
                this.endRoom()
        );

        this.usersViewModel = new ViewModelProvider(activity).get(UsersViewModel.class);
        final RecyclerView usersRecyclerView = view.findViewById(R.id.setSeenUsersFragment_list_recyclerView);


        this.usersViewModel.setLocalUserIndex(LobbyServer.SERVER_USER_ID);
        this.usersViewModel.setLocalUserPostfix(activity.getString(R.string.local_user_postfix));
        this.usersViewModel.getUsers().observe(this.getViewLifecycleOwner(),
                users -> this.usersRecyclerAdapter.setUsers(new ArrayList<>(Objects.requireNonNull(this.usersViewModel.getUsers().getValue()).values())));

        this.usersRecyclerAdapter = new SetSeenUsersRecyclerAdapter();
        usersRecyclerView.setAdapter(this.usersRecyclerAdapter);

        // No network
        if (!NetworkStatusHelper.getContextSingleton().isConnectedToWifi()) {
            this.hideNetworkInfo();
            this.usersViewModel.update(LobbyServer.SERVER_USER_ID, new UserSettings(activity).getUserName());
            return;
        }

        // Used to connect from the outside when the server is the emulator
        final String address = NetworkStatusHelper.getContextSingleton().getPublicAddress();
        if (address == null) {
            this.hideNetworkInfo();
            return;
        }

        // Tries to start the server
        this.server = new LobbyServer(new UserSettings(activity).getUserName(), this.movie);
        this.server.addOnUserReceived(this.usersViewModel);
        if (!this.server.start()) {
            this.hideNetworkInfo();
            return;
        }

        // Server started, enable QR code and text
        final MovieQrGenerator movieQrGenerator = new MovieQrGenerator();
        view.<ImageView>findViewById(R.id.setSeenMasterFragment_qr_imageView)
                .setImageBitmap(movieQrGenerator.generate(address, LobbyCommonNetwork.LISTEN_PORT));
    }

    @Override
    public void onStop() {
        if (this.server != null) {
            this.server.stop();
        }
        super.onStop();
    }

    private void endRoom() {
        if (this.server != null) {
            this.server.closeLobby();
        }
        this.movie.setSeen(true);
        this.movie.setSeenWith(this.usersViewModel.toString());
        new MoviesRepository().insertMovie(this.movie);
        this.requireActivity().finish();
    }

    private void hideNetworkInfo() {
        Toast.makeText(this.requireActivity(), R.string.not_connected_wifi, Toast.LENGTH_LONG).show();
        this.requireView().findViewById(R.id.setSeenMasterFragment_networkSection_layout).setVisibility(View.GONE);
    }
}
