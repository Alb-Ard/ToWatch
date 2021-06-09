package com.albard.towatch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.albard.towatch.database.MoviesDatabase;
import com.albard.towatch.settings.UserSettings;
import com.albard.towatch.utilities.Fragments;
import com.albard.towatch.utilities.NetworkStatusHelper;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            return;
        }

        NetworkStatusHelper.initializeSingleton(this);
        MoviesDatabase.initializeSingleton(this);

        if (!UserSettings.exists(this)) {
            this.startActivity(new Intent(this, FirstStartActivity.class));
            return;
        }

        this.setSupportActionBar(this.findViewById(R.id.mainActivity_parent_toolbar));

        Fragments.switchFragments(this,
                R.id.mainActivity_parent_containerView,
                new HomeFragment());
    }
}