package com.albard.towatch.seenlobby;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.albard.towatch.database.Movie;

import com.albard.towatch.utilities.NetworkStatusHelper;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class LobbyServer extends LobbyCommonNetwork {
    public static final int SERVER_USER_ID = -1;

    private final Movie movie;
    private final Map<Connection, String> names = new HashMap<>();
    private final Set<OnUserReceived> onUserReceivedSet = new HashSet<>();

    private Server server;
    private int disconnectCount;

    public LobbyServer(@NonNull final String userName, @NonNull final Movie movie) {
        super(userName);
        this.movie = movie;
    }

    public boolean start() {
        try {
            this.disconnectCount = -1;
            this.server.addListener(this);
            this.server.start();
            this.server.bind(LobbyCommonNetwork.LISTEN_PORT);
            this.names.put(null, this.getUserName());
            this.syncClientList();
            Log.i(getClass().getName(), "Server started on ip:" + NetworkStatusHelper.getContextSingleton().getPublicAddress() + " port:" + LobbyCommonNetwork.LISTEN_PORT);
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
            this.stop();
            return false;
        }
    }

    public void stop() {
        this.server.stop();
    }

    @Override
    public void connected(final Connection connection) {
        final MovieInfoRequest movieInfo = new MovieInfoRequest();
        movieInfo.serializedInfo = this.getSerializer().serialize(this.movie);
        connection.sendTCP(movieInfo);
        final ClientInfoRequest userRequest = new ClientInfoRequest();
        userRequest.mode = ClientInfoRequest.REQUEST;
        userRequest.id = connection.getID();
        connection.sendTCP(userRequest);
    }

    @Override
    public void disconnected(final Connection connection) {
        // I'm closing the lobby, don't send any messages
        if (this.disconnectCount >= 0) {
            return;
        }

        final ClientInfoRequest request = new ClientInfoRequest();
        request.mode = ClientInfoRequest.REMOVE;
        request.id = connection.getID();
        this.server.sendToAllTCP(request);
    }

    public void addOnUserReceived(@Nullable final OnUserReceived onUserReceived) {
        this.onUserReceivedSet.add(onUserReceived);
    }

    public void closeLobby() {
        final CloseLobbyRequest request = new CloseLobbyRequest();
        this.disconnectCount = this.server.getConnections().size();
        new Thread(() -> this.server.sendToAllTCP(request)).start();
    }

    @Override
    protected EndPoint initializeEndPoint() {
        this.server = new Server();
        return this.server;
    }

    @Override
    protected void handleClientInfo(@NonNull final Connection from, @NonNull final ClientInfoRequest req) {
        switch (req.mode) {
            case ClientInfoRequest.REMOVE:
                this.names.remove(from);
                final ClientInfoRequest request = new ClientInfoRequest();
                request.mode = ClientInfoRequest.REMOVE;
                request.id = req.id;
                this.server.sendToAllTCP(request);
                this.postToMainThread(() -> {
                    for (final OnUserReceived onUserReceived : this.onUserReceivedSet) {
                        onUserReceived.remove(req.id);
                    }
                });
                break;
            case ClientInfoRequest.UPDATE:
                if (!Objects.equals(this.names.put(from, req.name), req.name)) {
                    this.syncClientList();
                }
                break;
        }
    }

    @Override
    protected void handleCloseLobby(@NonNull final Connection connection, @NonNull final CloseLobbyRequest info) {
        super.handleCloseLobby(connection, info);
        this.disconnectCount--;
        if (this.disconnectCount == 0) {
            this.stop();
        }
    }

    private void syncClientList() {
        for (final Map.Entry<Connection, String> user : this.names.entrySet()) {
            final ClientInfoRequest request = new ClientInfoRequest();
            request.mode = ClientInfoRequest.UPDATE;
            request.name = user.getValue();
            request.id = user.getKey() == null ? LobbyServer.SERVER_USER_ID : user.getKey().getID();
            this.server.sendToAllTCP(request);
            this.postToMainThread(() -> {
                for (final OnUserReceived onUserReceived : this.onUserReceivedSet) {
                    onUserReceived.update(request.id, request.name);
                }
            });
        }
    }
}
