package com.albard.towatch;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.albard.towatch.utilities.ActionBarSetup;

public class InfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_info);
        ActionBarSetup.setBackButtonEnabled(this, true);
    }
}
