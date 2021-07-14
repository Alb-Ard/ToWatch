package com.albard.towatch.utilities;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

public final class Fragments {
    private Fragments() {}

    // Adds fragment to back stack
    public static void switchFragments(@NonNull final FragmentActivity activity,
                                       @IdRes final int containerId,
                                       @NonNull final Fragment fragment,
                                       @Nullable final String stateName,
                                       @Nullable final String tag) {
        Log.d(Fragments.class.getName(), "Switching to fragment " + fragment.getClass().getName() + " (" + stateName + ")");
        Utils.hideKeyboard(activity);
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment, tag)
                .addToBackStack(stateName)
                .commit();
        activity.getSupportFragmentManager().executePendingTransactions();
    }

    // Doesn't add fragment to back stack
    public static void switchFragments(@NonNull final FragmentActivity activity,
                                            @IdRes final int containerId,
                                            @NonNull final Fragment fragment) {
        Log.d(Fragments.class.getName(), "Switching to fragment " + fragment.getClass().getName());
        Utils.hideKeyboard(activity);
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment)
                .commitNow();
    }

    public static String getTransactionName(@NonNull final Object obj, @NonNull final String... args) {
        return Fragments.getTransactionName(obj.getClass(), args);
    }

    public static String getTransactionName(@NonNull final Class<?> objClass, @NonNull final String... args) {
        final StringBuilder builder = new StringBuilder(objClass.getName());
        for (@NonNull final String arg : args) {
            builder.append('_')
                    .append(arg);
        }
        return builder.toString();
    }
}
