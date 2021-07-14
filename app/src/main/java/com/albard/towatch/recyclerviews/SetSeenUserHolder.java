package com.albard.towatch.recyclerviews;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SetSeenUserHolder extends RecyclerView.ViewHolder {
    public SetSeenUserHolder(@NonNull final View itemView) {
        super(itemView);
    }

    public void bindName(@NonNull final String name) {
        ((TextView) this.itemView).setText(name);
    }
}
