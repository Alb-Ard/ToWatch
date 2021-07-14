package com.albard.towatch.recyclerviews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.albard.towatch.R;
import com.albard.towatch.seenlobby.UsersViewModel;

import java.util.List;

public class SetSeenUsersRecyclerAdapter extends RecyclerView.Adapter<SetSeenUserHolder> {
    private List<String> users;

    @NonNull
    @Override
    public SetSeenUserHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View holderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setseen_user,
                parent,
                false);
        return new SetSeenUserHolder(holderView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SetSeenUserHolder holder, final int position) {
        holder.bindName(this.users.get(position));
    }

    @Override
    public int getItemCount() {
        return this.users.size();
    }

    public void setUsers(@NonNull final List<String> users) {
        this.users = users;
        this.notifyDataSetChanged();
    }
}
