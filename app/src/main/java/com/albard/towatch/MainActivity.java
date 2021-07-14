package com.albard.towatch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.albard.towatch.database.MoviesDatabase;
import com.albard.towatch.settings.UserSettings;
import com.albard.towatch.utilities.Fragments;
import com.albard.towatch.utilities.NetworkStatusHelper;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        this.setSupportActionBar(this.findViewById(R.id.mainActivity_parent_toolbar));

        if (savedInstanceState != null) {
            return;
        }

        if (!UserSettings.exists(this)) {
            this.startActivity(new Intent(this, FirstStartActivity.class));
            this.finish();
            return;
        }

        NetworkStatusHelper.initializeContextSingleton(this);
        MoviesDatabase.initializeSingleton(this);

        Fragments.switchFragments(this,
                R.id.mainActivity_parent_containerView,
                new HomeFragment());
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        this.getMenuInflater().inflate(R.menu.base, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.detailsMenu_settings_menuItem:
                SettingsActivity.launch(this);
                return true;
            case R.id.detailsMenu_info_menuItem:
                this.startActivity(new Intent(this, InfoActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}