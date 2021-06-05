package com.albard.towatch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.albard.towatch.utilities.Fragments;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            return;
        }

        Fragments.switchFragments(this, R.id.mainActivity_parent_containerView, new HomeFragment(), null);
    }
}