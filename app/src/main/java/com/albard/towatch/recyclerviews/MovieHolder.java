package com.albard.towatch.recyclerviews;


import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.albard.towatch.R;
import com.albard.towatch.database.Movie;
import com.albard.towatch.database.MovieImagesRepository;
import com.albard.towatch.utilities.NetworkStatusHelper;

public class MovieHolder extends RecyclerView.ViewHolder {
    private final TextView nameTextView;
    private final ImageView seenImageView;
    private final ImageView posterImageView;
    private final TextView noImageTextView;

    private Movie movie;

    @FunctionalInterface
    public interface OnClickListener {
        void performed(@NonNull final Movie movie);
    }

    public MovieHolder(@NonNull final View itemView) {
        super(itemView);
        this.nameTextView = itemView.findViewById(R.id.itemMovie_movieName_textView);
        this.seenImageView = itemView.findViewById(R.id.itemMovie_seen_imageView);
        this.posterImageView = itemView.findViewById(R.id.itemMovie_movieImage_imageView);
        this.noImageTextView = itemView.findViewById(R.id.itemMovie_noImage_textView);
    }

    public void bindMovie(@NonNull final Movie movie) {
        this.movie = movie;

        this.nameTextView.setText(movie.getName());
        this.seenImageView.setVisibility(movie.getSeen() ? View.VISIBLE : View.GONE);

        final Bitmap image = MovieImagesRepository.getSingleton().getMoviePoster(movie, NetworkStatusHelper.getContextSingleton().isConnected());
        if (image == null) {
            this.posterImageView.setVisibility(View.GONE);
            this.noImageTextView.setVisibility(View.VISIBLE);
            return;
        }
        this.posterImageView.setVisibility(View.VISIBLE);
        this.noImageTextView.setVisibility(View.GONE);
        this.posterImageView.setImageBitmap(image);
    }

    public void setOnClickListener(@Nullable final OnClickListener listener) {
        this.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.performed(this.movie);
            }
        });
    }
}
