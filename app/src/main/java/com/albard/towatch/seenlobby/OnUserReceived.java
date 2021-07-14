package com.albard.towatch.seenlobby;

import androidx.annotation.NonNull;

public interface OnUserReceived {
    void update(final int id, @NonNull final String user);
    void remove(final int id);

    public static class OnUserReceivedImpl implements OnUserReceived {
        @Override
        public void update(final int id, @NonNull final String user) {

        }

        @Override
        public void remove(final int id) {

        }
    }
}
