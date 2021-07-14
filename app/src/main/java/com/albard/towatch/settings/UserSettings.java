package com.albard.towatch.settings;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.albard.towatch.BuildConfig;

public final class UserSettings {
    private static final String PREFS_FILE_NAME = BuildConfig.APPLICATION_ID + ".USERSETTINGS";
    private static final String PREFS_FILTER_NAME = "filter_adult";
    private static final String PREFS_USERNAME_NAME = "username";
    private final Context context;

    private boolean adultFilter;
    private String userName;

    public static boolean exists(@NonNull final Context context) {
        final SharedPreferences preferences = context.getSharedPreferences(UserSettings.PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return preferences.contains(UserSettings.PREFS_FILTER_NAME);
    }

    public UserSettings(@NonNull final Context context) {
        this.context = context;
        final SharedPreferences preferences = context.getSharedPreferences(UserSettings.PREFS_FILE_NAME, Context.MODE_PRIVATE);
        this.adultFilter = preferences.getBoolean(UserSettings.PREFS_FILTER_NAME, true);
        this.userName = preferences.getString(UserSettings.PREFS_USERNAME_NAME, "Unnamed");
    }

    public boolean getAdultFilter() {
        return this.adultFilter;
    }

    @NonNull
    public String getUserName() {
        return this.userName;
    }

    public void applySettings(final boolean adultFilter, @NonNull final String userName) {
        this.adultFilter = adultFilter;
        this.userName = userName;

        final SharedPreferences preferences = this.context.getSharedPreferences(UserSettings.PREFS_FILE_NAME, Context.MODE_PRIVATE);
        preferences.edit()
                .putBoolean(UserSettings.PREFS_FILTER_NAME, this.adultFilter)
                .putString(UserSettings.PREFS_USERNAME_NAME, this.userName)
                .apply();
    }
}
