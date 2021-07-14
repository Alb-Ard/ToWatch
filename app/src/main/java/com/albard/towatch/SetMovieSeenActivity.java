package com.albard.towatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.albard.towatch.database.Movie;
import com.albard.towatch.database.MoviesDatabase;
import com.albard.towatch.database.MoviesRepository;
import com.albard.towatch.utilities.ActionBarSetup;
import com.albard.towatch.utilities.Fragments;
import com.albard.towatch.utilities.MovieQrGenerator;
import com.albard.towatch.utilities.NetworkStatusHelper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class SetMovieSeenActivity extends AppCompatActivity {
    public static final String EXTRA_MODE = BuildConfig.APPLICATION_ID + ".MODE";
    public static final String EXTRA_MOVIE_ID = BuildConfig.APPLICATION_ID + ".MOVIE_ID";
    public static final int MASTER = 0;
    public static final int EXTERNAL = 1;

    private int mode;
    private MovieQrGenerator.Result qrResult;
    private boolean initialized;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_setmovieseen);

        if (savedInstanceState != null) {
            return;
        }

        NetworkStatusHelper.initializeContextSingleton(this);
        MoviesDatabase.initializeSingleton(this);
        ActionBarSetup.setBackButtonEnabled(this, true);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final Bundle bundle = this.getIntent().getExtras();
        if (bundle == null) {
            Log.e(getClass().getName(), "Invalid extras");
            this.finish();
            return;
        }

        this.mode = bundle.getInt(SetMovieSeenActivity.EXTRA_MODE, SetMovieSeenActivity.MASTER);
        switch (this.mode) {
            case SetMovieSeenActivity.MASTER:
                final Movie movie = new MoviesRepository().getMovie(bundle.getInt(SetMovieSeenActivity.EXTRA_MOVIE_ID));
                Fragments.switchFragments(this,
                        R.id.setMovieSeenActivity_parent_fragmentView,
                        new SetSeenMasterFragment(movie));
                break;
            case SetMovieSeenActivity.EXTERNAL:
                new IntentIntegrator(this)
                        .setBeepEnabled(false)
                        .initiateScan();
                break;
            default:
                Log.e(getClass().getName(), "Invalid mode");
                this.finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (this.mode == SetMovieSeenActivity.EXTERNAL && this.qrResult != null) {
            Fragments.switchFragments(this,
                    R.id.setMovieSeenActivity_parent_fragmentView,
                    new SetSeenExternalFragment(this.qrResult.getIp(), this.qrResult.getPort()));
            // The QR scanner makes the activity resume when it's over, so we'll need to reset the
            // flag to signal that it's still the first time showing the activity
            this.initialized = false;
        }

        // Checks if it's the first time showing the activity
        if (!this.initialized) {
            this.initialized = true;
            return;
        }

        // Network will close in stand-by, so we'll need to reconnect from the start.
        this.finish();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if(result.getContents() == null) {
            this.finish();
            return;
        }

        this.qrResult = new MovieQrGenerator().parse(result.getContents());
    }

    public static void start(@NonNull final Context from, final int mode, final int movieId) {
        from.startActivity(new Intent(from, SetMovieSeenActivity.class)
                .putExtra(SetMovieSeenActivity.EXTRA_MODE, mode)
                .putExtra(SetMovieSeenActivity.EXTRA_MOVIE_ID, movieId));
    }
}
