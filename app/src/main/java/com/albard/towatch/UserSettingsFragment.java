package com.albard.towatch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.albard.towatch.settings.UserSettings;

public class UserSettingsFragment extends Fragment {
    private UserSettings userSettings;

    private CheckBox adultFilterCheckBox;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_usersettings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final FragmentActivity activity = this.getActivity();
        if (activity == null) {
            return;
        }

        this.userSettings = new UserSettings(activity);

        this.adultFilterCheckBox = view.findViewById(R.id.userSettingsFragment_filterAdult_checkBox);
        this.adultFilterCheckBox.setChecked(this.getUserSettings().getAdultFilter());
    }

    protected void applySettings() {
        this.getUserSettings().applySettings(this.adultFilterCheckBox.isChecked());
    }

    protected UserSettings getUserSettings() {
        return this.userSettings;
    }
}
