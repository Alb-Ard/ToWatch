package com.albard.towatch.utilities;

import android.app.Activity;

import android.content.ContentResolver;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class BitmapUtils {
    private BitmapUtils() {

    }

    @Nullable
    public static Bitmap getBitmapFromUri(@NonNull final Context context, @NonNull final Uri uri){
        final ContentResolver resolver = context.getContentResolver();

        try (final InputStream stream = resolver.openInputStream(uri)) {
            final Bitmap bitmap = BitmapFactory.decodeStream(stream);
            stream.close();
            return bitmap;
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @NonNull
    public static Bitmap drawableToBitmap(@NonNull final Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        final Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Uri saveBitmap(@NonNull final Activity activity, @NonNull final Bitmap bitmap, @Nullable final String defaultName) {
        final ContentResolver contentResolver = activity.getContentResolver();
        final ContentValues contentValues = new ContentValues();
        final String name = defaultName != null ? defaultName : SimpleDateFormat.getDateTimeInstance().format(new Date()) + ".jpg";
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        final Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        try (final OutputStream stream = contentResolver.openOutputStream(uri)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return uri;
    }

    public static void deleteBitmap(@NonNull final Activity activity, @Nullable final String name) {
        final ContentResolver contentResolver = activity.getContentResolver();
        final ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        final Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        final File file = new File(uri.toString());
        file.delete();
    }
}
