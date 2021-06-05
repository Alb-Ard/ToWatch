package com.albard.towatch.utilities;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public final class Fragments {
    private Fragments() {}

    public static void switchFragments(@NonNull final FragmentActivity activity,
                                       @IdRes final int containerId,
                                       @NonNull final Fragment fragment,
                                       @Nullable final String tag)
    {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment, tag)
                .commitNow();
    }
}
