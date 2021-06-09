package com.albard.towatch.settings;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.albard.towatch.BuildConfig;

public final class UserSettings {
    private static final String PREFS_FILE_NAME = BuildConfig.APPLICATION_ID + ".USERSETTINGS";
    private static final String PREFS_FILTER_NAME = "filter_adult";
    private final Context context;

    private boolean adultFilter;

    public static boolean exists(@NonNull final Context context) {
        final SharedPreferences preferences = context.getSharedPreferences(UserSettings.PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return preferences.contains(UserSettings.PREFS_FILTER_NAME);
    }

    public UserSettings(@NonNull final Context context) {
        this.context = context;
        final SharedPreferences preferences = context.getSharedPreferences(UserSettings.PREFS_FILE_NAME, Context.MODE_PRIVATE);
        this.adultFilter = preferences.getBoolean(UserSettings.PREFS_FILTER_NAME, true);
    }

    public boolean getAdultFilter() {
        return this.adultFilter;
    }

    public void applySettings(final boolean adultFilter) {
        this.adultFilter = adultFilter;

        final SharedPreferences preferences = this.context.getSharedPreferences(UserSettings.PREFS_FILE_NAME, Context.MODE_PRIVATE);
        preferences.edit()
                .putBoolean(UserSettings.PREFS_FILTER_NAME, this.adultFilter)
                .apply();
    }
}
