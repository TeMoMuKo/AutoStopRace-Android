package pl.temomuko.autostoprace.data.local.photo;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.injection.AppContext;
import pl.temomuko.autostoprace.util.LogUtil;
import rx.Observable;

@Singleton
public class FileController {

    private static final String TAG = FileController.class.getSimpleName();
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 8;

    private final Context context;

    @Inject
    public FileController(@AppContext Context context) {
        this.context = context;
    }

    public Observable<String> getBase64FromUri(@Nullable final Uri uri) {
        return Observable.fromCallable(() -> {
            if (uri == null) {
                return null;
            }
            InputStream inputStream = null;
            ByteArrayOutputStream byteArrayOutputStream = null;
            try {
                inputStream = context.getContentResolver().openInputStream(uri);
                byteArrayOutputStream = new ByteArrayOutputStream();
                if (inputStream != null) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                    }
                    final String mime = context.getContentResolver().getType(uri);
                    return "data:" + mime + ";base64," + Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP);
                } else {
                    throw new IOException("Couldn't retrieve input stream from given uri.");
                }
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
            }
        });
    }

    /**
     * Check if file size under given uri is less than 1mb and returns the same uri else return
     * FileToBigException
     */
    public Observable<Uri> checkFileSizeLessThanMax(final Uri uri) {
        return Observable.fromCallable(() -> {
            AssetFileDescriptor assetFileDescriptor = context.getContentResolver().openAssetFileDescriptor(uri, "r");
            long size = assetFileDescriptor.getLength();
            LogUtil.d(TAG, String.format("%s file size: %s bytes", uri.getLastPathSegment(), Long.toString(size)));
            if (size > MAX_FILE_SIZE) {
                throw new FileToBigException("Provided file exceeds size limit: " + MAX_FILE_SIZE);
            }

            return uri;
        });
    }

    public static class FileToBigException extends Exception {

        private FileToBigException(String s) {
            super(s);
        }
    }
}
