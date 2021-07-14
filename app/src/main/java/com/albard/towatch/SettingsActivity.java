package com.albard.towatch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.albard.towatch.utilities.ActionBarSetup;
import com.albard.towatch.utilities.Fragments;
import com.albard.towatch.utilities.Utils;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_settings);

        if (savedInstanceState != null) {
            return;
        }

        this.setSupportActionBar(this.findViewById(R.id.settingsActivity_parent_toolbar));

        Fragments.switchFragments(this, R.id.settingsActivity_parent_fragmentView, new SettingsFragment());
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        this.getMenuInflater().inflate(R.menu.base, menu);
        ActionBarSetup.setupMenu(menu, false, false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            Utils.hideKeyboard(this);
            this.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void launch(@NonNull final Context from) {
        from.startActivity(new Intent(from, SettingsActivity.class));
    }
}
