package com.albard.towatch.moviesapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.albard.towatch.database.Movie;
import com.albard.towatch.utilities.NetworkStatusHelper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TmdbMovieImagesProvider extends MovieImagesProvider {
    private final ExecutorService executor;

    public TmdbMovieImagesProvider() {
        this.executor = Executors.newSingleThreadExecutor();
    }

    @Nullable
    @Override
    public Bitmap request(@NonNull final Movie movie) {
        if (!NetworkStatusHelper.getSingleton().isConnected()) {
            NetworkStatusHelper.getSingleton().showRequestDialog();
            return null;
        }

        try {
            return this.executor.submit(() -> {
                final HttpClient httpClient = new DefaultHttpClient();
                try {
                    final HttpResponse response = httpClient.execute(new HttpGet(movie.getImageUri()));
                    final BufferedHttpEntity httpEntity = new BufferedHttpEntity(response.getEntity());
                    final int replyLength = (int)httpEntity.getContentLength();
                    final byte[] content = new byte[replyLength];
                    final int readLength = httpEntity.getContent().read(content, 0, replyLength);
                    return BitmapFactory.decodeByteArray(content, 0, readLength);
                } catch (final IOException e) {
                    throw new RuntimeException("Image download failed", e);
                }
            }).get();
        } catch (final ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
