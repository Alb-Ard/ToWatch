package com.albard.towatch.viewmodels;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SingleValueViewModel<T> extends ViewModel {
    private T value;

    public SingleValueViewModel() {
        this(null);
    }

    public SingleValueViewModel(@Nullable final T defaultValue) {
        this.value = defaultValue;
    }

    public T get() {
        return this.value;
    }

    public void set(@Nullable final T value) {
        this.value = value;
    }
}
