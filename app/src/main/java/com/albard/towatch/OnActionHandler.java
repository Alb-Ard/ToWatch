package com.albard.towatch;

@FunctionalInterface
public interface OnActionHandler<T> {
    void executed(final T value);
}
