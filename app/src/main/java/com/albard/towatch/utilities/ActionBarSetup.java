package com.albard.towatch.utilities;

import android.app.Activity;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.albard.towatch.R;

public final class ActionBarSetup {
    private ActionBarSetup() {}

    public static void setupMenu(@NonNull final Menu menu, final boolean showDetailsOptions, final boolean showSettings) {
        menu.findItem(R.id.detailsMenu_edit_menuItem).setVisible(showDetailsOptions);
        menu.findItem(R.id.detailsMenu_delete_menuItem).setVisible(showDetailsOptions);
        menu.findItem(R.id.detailsMenu_settings_menuItem).setVisible(showSettings);
    }

    public static void setBackButtonEnabled(@NonNull final Activity activity, final boolean enabled) {
        final ActionBar actionBar = ActionBarSetup.getSupportActionBar(activity);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(enabled);
        }
    }

    public static void setTitle(@NonNull final Activity activity, @StringRes final int textResource) {
        final ActionBar actionBar = ActionBarSetup.getSupportActionBar(activity);
        if (actionBar != null) {
            actionBar.setTitle(textResource);
        }
    }

    @Nullable
    private static ActionBar getSupportActionBar(@NonNull final Activity activity) {
        return activity instanceof AppCompatActivity
                ? ((AppCompatActivity) activity).getSupportActionBar()
                : null;
    }
}
