package com.albard.towatch.utilities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.albard.towatch.OnActionHandler;
import com.google.android.material.snackbar.Snackbar;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.function.Consumer;
import java.util.function.Function;

public class NetworkStatusHelper {
    private final static boolean USE_NETWORK_CALLBACK = Build.VERSION.SDK_INT > Build.VERSION_CODES.N;
    private final static String LOG_TAG = NetworkStatusHelper.class.getName();

    private static NetworkStatusHelper SINGLETON;

    private final ConnectivityManager connectivityManager;

    private ConnectivityManager.NetworkCallback networkStatusCallback;
    private boolean isConnectedToWifi = false;
    private boolean isConnectedToLTE = false;
    private boolean isDisposed = false;

    public static NetworkStatusHelper initializeContextSingleton(@NonNull final Activity activity) {
        if (NetworkStatusHelper.SINGLETON != null) {
            NetworkStatusHelper.SINGLETON.stop();
        }
        NetworkStatusHelper.SINGLETON = new NetworkStatusHelper(activity);
        return NetworkStatusHelper.getContextSingleton();
    }

    public static NetworkStatusHelper getContextSingleton() {
        if (NetworkStatusHelper.SINGLETON == null) {
            throw new RuntimeException("Singleton not initialized");
        }
        return NetworkStatusHelper.SINGLETON;
    }

    private NetworkStatusHelper(@NonNull final Activity activity) {
        this.connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (NetworkStatusHelper.USE_NETWORK_CALLBACK) {
            this.checkConnectivityManager();
            this.networkStatusCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull final Network network) {
                    super.onAvailable(network);
                    if (NetworkStatusHelper.this.connectivityManager.getNetworkCapabilities(network).hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        NetworkStatusHelper.this.isConnectedToWifi = true;
                    } else if (NetworkStatusHelper.this.connectivityManager.getNetworkCapabilities(network).hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        NetworkStatusHelper.this.isConnectedToLTE = true;
                    }
                }

                @Override
                public void onLost(@NonNull final Network network) {
                    super.onLost(network);
                    if (NetworkStatusHelper.this.connectivityManager.getNetworkCapabilities(network).hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        NetworkStatusHelper.this.isConnectedToWifi = false;
                    } else if (NetworkStatusHelper.this.connectivityManager.getNetworkCapabilities(network).hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        NetworkStatusHelper.this.isConnectedToLTE = false;
                    }
                }
            };
            this.connectivityManager.registerDefaultNetworkCallback(this.networkStatusCallback);
        }

        Log.d(NetworkStatusHelper.LOG_TAG, "Network helper started, is using callback? " + NetworkStatusHelper.USE_NETWORK_CALLBACK);
    }

    public void stop() {
        this.checkNotDisposed();
        this.isConnectedToWifi = false;
        this.isConnectedToLTE = false;
        this.isDisposed = true;
        if (NetworkStatusHelper.USE_NETWORK_CALLBACK) {
            this.checkConnectivityManager();
            this.connectivityManager.unregisterNetworkCallback(this.networkStatusCallback);
        }
        Log.d(NetworkStatusHelper.LOG_TAG, "Network helper stopped");
    }

    public void executeIfNotConnected(@NonNull final OnActionHandler<Activity> action, @NonNull final Activity activity) {
        this.checkNotDisposed();
        if (this.isConnected()) {
            return;
        }
        action.executed(activity);
    }

    public boolean isConnected() {
        this.checkNotDisposed();
        return this.isConnectedToLTE() || this.isConnectedToWifi();
    }

    public boolean isConnectedToWifi() {
        this.checkNotDisposed();
        if (!NetworkStatusHelper.USE_NETWORK_CALLBACK) {
            final NetworkInfo networkInfo = this.connectivityManager.getActiveNetworkInfo();
            this.isConnectedToWifi = networkInfo != null
                    && networkInfo.getType() == ConnectivityManager.TYPE_WIFI
                    && networkInfo.isConnected();
        }

        return this.isConnectedToWifi;
    }

    public boolean isConnectedToLTE() {
        this.checkNotDisposed();
        if (!NetworkStatusHelper.USE_NETWORK_CALLBACK) {
            final NetworkInfo networkInfo = this.connectivityManager.getActiveNetworkInfo();
            this.isConnectedToLTE = networkInfo != null
                    && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE
                    && networkInfo.isConnected();
        }

        return this.isConnectedToLTE;
    }

    @Nullable
    public String getPublicAddress() {
        if (!this.isConnected()) {
            return null;
        }

        try {
            final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while(interfaces.hasMoreElements()) {
                final NetworkInterface networkInterface = interfaces.nextElement();
                final Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    final InetAddress address = addresses.nextElement();
                    if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (final SocketException e) {
            e.printStackTrace();
        }
        return null;
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
