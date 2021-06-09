package com.albard.towatch.utilities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.albard.towatch.R;
import com.google.android.material.snackbar.Snackbar;

public class NetworkStatusHelper {
    private final static boolean USE_NETWORK_CALLBACK = Build.VERSION.SDK_INT > Build.VERSION_CODES.N;
    private final static String LOG_TAG = NetworkStatusHelper.class.getName();

    private static NetworkStatusHelper SINGLETON;

    private final ConnectivityManager connectivityManager;
    private final Snackbar notConnectedSnackbar;

    private ConnectivityManager.NetworkCallback networkStatusCallback;
    private boolean isConnected = false;
    private boolean isDisposed = false;

    public static NetworkStatusHelper initializeSingleton(@NonNull final Activity activity) {
        if (NetworkStatusHelper.SINGLETON != null) {
            NetworkStatusHelper.SINGLETON.stop();
        }
        NetworkStatusHelper.SINGLETON = new NetworkStatusHelper(activity);
        return NetworkStatusHelper.getSingleton();
    }

    public static NetworkStatusHelper getSingleton() {
        if (NetworkStatusHelper.SINGLETON == null) {
            throw new RuntimeException("Singleton not initialized");
        }
        return NetworkStatusHelper.SINGLETON;
    }

    private NetworkStatusHelper(@NonNull final Activity activity) {
        this.connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        this.notConnectedSnackbar = Snackbar.make(activity.getWindow().getDecorView(), "No internet available", Snackbar.LENGTH_LONG)
                .setAction("Go to settings", v -> {
                    final Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    final ComponentName componentName = intent.resolveActivity(activity.getPackageManager());
                    if (componentName == null) {
                        return;
                    }
                    activity.startActivity(intent);
                });

        if (NetworkStatusHelper.USE_NETWORK_CALLBACK) {
            this.checkConnectivityManager();
            this.networkStatusCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull final Network network) {
                    super.onAvailable(network);
                    NetworkStatusHelper.this.isConnected = true;
                    if (NetworkStatusHelper.this.notConnectedSnackbar.isShown()) {
                        NetworkStatusHelper.this.notConnectedSnackbar.dismiss();
                    }
                }

                @Override
                public void onLost(@NonNull final Network network) {
                    super.onLost(network);
                    NetworkStatusHelper.this.isConnected = false;
                }
            };
            this.connectivityManager.registerDefaultNetworkCallback(this.networkStatusCallback);
        }

        Log.d(NetworkStatusHelper.LOG_TAG, "Network helper started, is using callback? " + NetworkStatusHelper.USE_NETWORK_CALLBACK);
    }

    public void stop() {
        this.checkNotDisposed();
        this.isConnected = false;
        this.isDisposed = true;
        if (this.notConnectedSnackbar.isShown()) {
            this.notConnectedSnackbar.dismiss();
        }
        if (NetworkStatusHelper.USE_NETWORK_CALLBACK) {
            this.checkConnectivityManager();
            this.connectivityManager.unregisterNetworkCallback(this.networkStatusCallback);
        }
        Log.d(NetworkStatusHelper.LOG_TAG, "Network helper stopped");

    }

    public void showRequestDialog() {
        this.checkNotDisposed();
        if (this.isConnected()) {
            return;
        }
        this.notConnectedSnackbar.show();
    }

    public boolean isConnected() {
        this.checkNotDisposed();
        if (!NetworkStatusHelper.USE_NETWORK_CALLBACK) {
            final NetworkInfo networkInfo = this.connectivityManager.getActiveNetworkInfo();
            this.isConnected = networkInfo != null && networkInfo.isConnected();
        }

        return this.isConnected;
    }

    private void checkNotDisposed() {
        if (this.isDisposed) {
            throw new RuntimeException("Helper was stopped");
        }
    }

    private void checkConnectivityManager() {
        if (this.connectivityManager == null) {
            throw new RuntimeException("ConnectivityManager is null");
        }
    }
}
