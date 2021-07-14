package com.albard.towatch.seenlobby;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.albard.towatch.database.Movie;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class LobbyCommonNetwork implements Listener {
    public static final int LISTEN_PORT = 42315;

    private final Looper mainLooper;

    @FunctionalInterface
    public interface OnMovieReceived {
        void receive(@NonNull final Movie movie);
    }

    @FunctionalInterface
    public interface OnDisconnect {
        void disconnected(final boolean completed);
    }

    private final ExecutorService executor;
    private final MovieSerializer serializer;
    private final String userName;

    public LobbyCommonNetwork(@NonNull final String userName) {
        final Kryo kryo = this.initializeEndPoint().getKryo();
        kryo.register(ClientInfoRequest.class);
        kryo.register(MovieInfoRequest.class);
        kryo.register(CloseLobbyRequest.class);
        this.serializer = new MovieSerializer();
        this.userName = userName;
        this.executor = Executors.newSingleThreadExecutor();
        this.mainLooper = Looper.getMainLooper();
    }

    @Override
    public final void received(final Connection connection, final Object object) {
        if (object instanceof MovieInfoRequest) {
            this.handleMovieInfo(connection, (MovieInfoRequest) object);
        } else if (object instanceof ClientInfoRequest) {
            this.handleClientInfo(connection, (ClientInfoRequest) object);
        } else if (object instanceof CloseLobbyRequest) {
            this.handleCloseLobby(connection, (CloseLobbyRequest) object);
        }
    }

    protected String getUserName() {
        return this.userName;
    }

    protected ExecutorService getExecutor() {
        return this.executor;
    }

    protected MovieSerializer getSerializer() {
        return this.serializer;
    }

    protected void handleMovieInfo(@NonNull final Connection connection, @NonNull final MovieInfoRequest info) {
    }

    protected void handleClientInfo(@NonNull final Connection connection, @NonNull final ClientInfoRequest info) {
    }

    protected  void handleCloseLobby(@NonNull final Connection connection, @NonNull final CloseLobbyRequest info) {
    }

    protected void postToMainThread(@NonNull final Runnable runnable) {
        synchronized (this.mainLooper) {
            new Handler(this.mainLooper).post(runnable);
        }
    }

    protected abstract EndPoint initializeEndPoint();
}
