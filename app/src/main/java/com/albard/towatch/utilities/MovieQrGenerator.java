package com.albard.towatch.utilities;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.albard.towatch.database.Movie;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class MovieQrGenerator {
    private static final String SEPARATOR = "\0";

    public static class Result {
        private final String ip;
        private final int port;

        public Result(@NonNull final String ip, final int port) {
            this.ip = ip;
            this.port = port;
        }

        @NonNull
        public String getIp() {
            return this.ip;
        }

        public int getPort() {
            return this.port;
        }
    }

    @Nullable
    public Bitmap generate(@NonNull final String originIp, @NonNull final int originPort) {
        try {
            final BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            final String content = originIp + SEPARATOR + originPort;
            return barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 400, 400);
        } catch(Exception e) {
            throw new RuntimeException("Error generating QR code", e);
        }
    }

    @NonNull
    public Result parse(@NonNull final String from) {
        final String[] parts = from.split(SEPARATOR);
        return new Result(parts[0], Integer.parseInt(parts[1]));
    }
}
