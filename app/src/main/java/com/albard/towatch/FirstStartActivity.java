package com.albard.towatch;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.albard.towatch.utilities.Fragments;

public class FirstStartActivity extends AppCompatActivity {
    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_firststart);

        if (savedInstanceState != null) {
            return;
        }

        Fragments.switchFragments(this,
                R.id.firstStartActivity_parent_containerView,
                new FirstStartFragment());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        if (item.getItemId() == R.id.detailsMenu_settings_menuItem) {
            SettingsActivity.launch(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
