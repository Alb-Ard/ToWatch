package com.albard.towatch;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class RandomTextView {
    final TextView textView;
    final List<String> values = new ArrayList<>();
    final Random rng = new Random();

    public RandomTextView(@NonNull final TextView textView) {
        this.textView = textView;
    }

    public RandomTextView addValue(@NonNull final String value) {
        this.values.add(value);
        return this;
    }

    public RandomTextView addValues(@NonNull final Collection<String> value) {
        this.values.addAll(value);
        return this;
    }

    public void extract() {
        this.textView.setText(this.values.get(this.rng.nextInt(this.values.size())));
    }

    public void extract(@Nullable final Object... params) {
        final String value = this.values.get(this.rng.nextInt(this.values.size()));
        this.textView.setText(String.format(value, params));
    }

    public TextView getTextView() {
        return this.textView;
    }
}
