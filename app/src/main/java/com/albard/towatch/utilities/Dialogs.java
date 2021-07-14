package com.albard.towatch.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.albard.towatch.R;

public final class Dialogs {
    private Dialogs() {}

    public static void showNotConnectedToWifi(@NonNull final Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.not_connected_wifi)
                .setPositiveButton(R.string.go_to_settings, (dialog, which) -> {
                    final Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    final ComponentName componentName = intent.resolveActivity(activity.getPackageManager());
                    if (componentName == null) {
                        return;
                    }
                    dialog.dismiss();
                    activity.startActivity(intent);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    public static void showNotConnected(@NonNull final Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.not_connected)
                .setPositiveButton(R.string.go_to_settings, (dialog, which) -> {
                    final Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    final ComponentName componentName = intent.resolveActivity(activity.getPackageManager());
                    if (componentName == null) {
                        return;
                    }
                    dialog.dismiss();
                    activity.startActivity(intent);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }
}
