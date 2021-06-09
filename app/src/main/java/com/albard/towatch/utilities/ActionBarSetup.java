package com.albard.towatch.utilities;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public final class ActionBarSetup {
    private ActionBarSetup() {}

    public static void setBackButtonEnabled(@NonNull final Activity activity, final boolean enabled) {
        if (!(activity instanceof AppCompatActivity)) {
            return;
        }

        final ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
        if (actionBar == null) {
            return;
        }

        actionBar.setDisplayHomeAsUpEnabled(enabled);
    }
}
