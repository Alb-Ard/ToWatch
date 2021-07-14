package com.albard.towatch.seenlobby;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.albard.towatch.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersViewModel extends ViewModel implements OnUserReceived {
    private final MutableLiveData<Map<Integer, String>> users;

    private int localUserIndex;
    private String localUserPostfix;

    public UsersViewModel() {
        this.users = new MutableLiveData<>();
        this.users.setValue(new HashMap<>());
    }

    @NonNull
    public LiveData<Map<Integer, String>> getUsers() {
        return this.users;
    }

    public void setUsers(@NonNull final Map<Integer, String> users) {
        this.users.setValue(users);
    }

    public void setLocalUserIndex(final int index) {
        this.localUserIndex = index;
    }

    public void setLocalUserPostfix(@NonNull final String postfix) {
        this.localUserPostfix = postfix;
    }

    @Override
    public void update(final int id, @NonNull final String user) {
        Log.i(getClass().getName(), "Updated user (id=" + id + ", name=" + user + ")");
        final Map<Integer, String> tempUsers = this.users.getValue();
        tempUsers.put(id, user);
        this.setUsers(tempUsers);
    }

    @Override
    public void remove(final int id) {
        final Map<Integer, String> tempUsers = this.users.getValue();
        tempUsers.remove(id);
        this.setUsers(tempUsers);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        final Map<Integer, String> users = this.users.getValue();
        if (users == null) {
            return "";
        }

        for (final Map.Entry<Integer, String> user : users.entrySet()) {
            builder.append(user.getValue());
            if (user.getKey() == this.localUserIndex) {
                builder.append(this.localUserPostfix);
            }
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }
}
