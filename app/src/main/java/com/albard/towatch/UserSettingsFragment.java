package com.albard.towatch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.albard.towatch.settings.UserSettings;
import com.google.android.material.textfield.TextInputLayout;

public class UserSettingsFragment extends Fragment {
    private UserSettings userSettings;

    private CheckBox adultFilterCheckBox;
    private TextInputLayout userNameTextLayout;
    private EditText userNameEditText;

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

        this.userNameEditText = view.findViewById(R.id.userSettingsFragment_userName_editText);
        this.userNameEditText.setText(this.getUserSettings().getUserName());

        this.userNameTextLayout = view.findViewById(R.id.userSettingsFragment_userName_textLayout);
        this.userNameTextLayout.setError(this.getString(R.string.invalid_name));
        this.userNameTextLayout.setErrorEnabled(false);

        this.adultFilterCheckBox = view.findViewById(R.id.userSettingsFragment_filterAdult_checkBox);
        this.adultFilterCheckBox.setChecked(this.getUserSettings().getAdultFilter());
    }

    protected boolean applySettings() {
        final String name = this.userNameEditText.getText().toString();
        if (name == null || name.length() == 0) {
            this.userNameTextLayout.setErrorEnabled(true);
            return false;
        }
        this.userNameTextLayout.setErrorEnabled(false);

        this.getUserSettings().applySettings(this.adultFilterCheckBox.isChecked(), this.userNameEditText.getText().toString());
        return true;
    }

    protected UserSettings getUserSettings() {
        return this.userSettings;
    }
}
