package pl.temomuko.autostoprace.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import retrofit.HttpException;

/**
 * Created by szymen on 2016-01-09.
 */
public class NetworkUtil {

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static boolean isHttpException(Throwable throwable, int statusCode) {
        return throwable instanceof HttpException && ((HttpException) throwable).code() == statusCode;
    }
}