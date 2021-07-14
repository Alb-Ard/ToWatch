package com.albard.towatch.seenlobby;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.albard.towatch.database.Movie;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.KryoNetException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class LobbyClient extends LobbyCommonNetwork {
    private final Set<OnUserReceived> onUserReceivedSet = new HashSet<>();

    private Client client;
    private int id;
    private OnMovieReceived onMovieReceived;
    private OnDisconnect onDisconnect;
    private boolean completed;


    public LobbyClient(@NonNull final String userName) {
        super(userName);
    }

    public boolean connect(@NonNull final String ip, final int port) {
        this.completed = false;
        this.client.addListener(this);
        this.client.start();
        try {
            return this.getExecutor().submit(() -> {
                try {
                    this.client.connect(5000, ip, port);
                    return true;
                } catch (final IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }).get();
        } catch (final ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void close() {
        this.client.stop();
    }

    public void addOnUserReceived(@NonNull final OnUserReceived onUserReceived) {
        this.onUserReceivedSet.add(onUserReceived);
    }

    @Nullable
    public OnMovieReceived getOnMovieReceived() {
        return this.onMovieReceived;
    }

    public void setOnMovieReceived(@Nullable final OnMovieReceived onMovieReceived) {
        this.onMovieReceived = onMovieReceived;
    }

    @Nullable
    public OnDisconnect getOnDisconnect() {
        return this.onDisconnect;
    }

    public void setOnDisconnect(@Nullable final OnDisconnect onDisconnect) {
        this.onDisconnect = onDisconnect;
    }

    public int getSharedId() {
        return this.id;
    }

    @Override
    public void disconnected(final Connection connection) {
        this.postToMainThread(() -> {
            if (this.onDisconnect != null) {
                this.onDisconnect.disconnected(this.completed);
            }
        });
    }

    @Override
    protected void handleMovieInfo(@NonNull final Connection connection, @NonNull final MovieInfoRequest info) {
        final Movie movie = this.getSerializer().deserialize(info.serializedInfo);
        this.postToMainThread(() -> {
            if (this.onMovieReceived != null) {
                this.onMovieReceived.receive(movie);
            }
        });
    }

    @Override
    protected void handleClientInfo(@NonNull final Connection connection, @NonNull final ClientInfoRequest info) {
        switch (info.mode) {
            case ClientInfoRequest.UPDATE:
                this.postToMainThread(() -> {
                    for (final OnUserReceived onUserReceived : this.onUserReceivedSet) {
                        onUserReceived.update(info.id, info.name);
                    }
                });
                break;
            case ClientInfoRequest.REMOVE:
                this.postToMainThread(() -> {
                    for (final OnUserReceived onUserReceived : this.onUserReceivedSet) {
                        onUserReceived.remove(info.id);
                    }
                });
                break;
            case ClientInfoRequest.REQUEST:
                this.id = info.id;
                final ClientInfoRequest reply = new ClientInfoRequest();
                reply.mode = ClientInfoRequest.UPDATE;
                reply.name = this.getUserName();
                reply.id = info.id;
                this.client.sendTCP(reply);
                break;
        }
    }

    @Override
    protected void handleCloseLobby(@NonNull final Connection connection, @NonNull final CloseLobbyRequest info) {
        super.handleCloseLobby(connection, info);
        this.completed = true;
        this.client.sendTCP(new CloseLobbyRequest());
        this.close();
    }

    @Override
    protected EndPoint initializeEndPoint() {
        this.client = new Client();
        return this.client;
    }
}
