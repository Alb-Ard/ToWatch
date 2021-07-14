package com.albard.towatch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.albard.towatch.utilities.ActionBarSetup;
import com.albard.towatch.utilities.Fragments;

public class SettingsFragment extends UserSettingsFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final FragmentActivity activity = this.getActivity();
        if (activity == null) {
            return;
        }

        ActionBarSetup.setBackButtonEnabled(activity, true);

        view.findViewById(R.id.settingsFragment_confirm_button).setOnClickListener(v ->
                this.applySettings()
        );
    }

    @Override
    protected boolean applySettings() {
        if (!super.applySettings()) {
            return false;
        }
        this.requireActivity().onBackPressed();
        return true;
    }
}
