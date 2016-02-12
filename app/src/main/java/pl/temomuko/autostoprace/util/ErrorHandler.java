package pl.temomuko.autostoprace.util;

import android.content.Context;

import java.io.IOException;
import java.net.SocketTimeoutException;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.model.ApiError;
import pl.temomuko.autostoprace.data.remote.StandardResponseException;
import pl.temomuko.autostoprace.injection.AppContext;
import retrofit2.Response;

/**
 * Created by szymen on 2016-01-27.
 */

@Singleton
public class ErrorHandler {

    private Context mContext;

    @Inject
    public ErrorHandler(@AppContext Context context) {
        mContext = context;
    }

    public String getMessage(Throwable throwable) {
        if (throwable instanceof StandardResponseException) {
            return getMessageFromResponse(((StandardResponseException) throwable).getResponse());
        } else {
            return getMessageFromRetrofitThrowable(throwable);
        }
    }

    public String getMessageFromResponse(Response<?> response) {
        ApiError apiError = new ApiError(response);
        switch (apiError.getStatus()) {
            case HttpStatus.NOT_FOUND:
                return mContext.getString(R.string.error_404);
            case HttpStatus.FORBIDDEN:
                return mContext.getString(R.string.error_403);
            case HttpStatus.UNAUTHORIZED:
                return mContext.getString(R.string.error_401);
            case HttpStatus.BAD_REQUEST:
                return mContext.getString(R.string.error_400);
            case HttpStatus.INTERNAL_SERVER_ERROR:
                return mContext.getString(R.string.error_500);
            case HttpStatus.BAD_GATEWAY:
                return mContext.getString(R.string.error_502);
            default:
                return mContext.getString(R.string.error_unknown);
        }
    }

    public String getMessageFromRetrofitThrowable(Throwable throwable) {
        if ((throwable instanceof SocketTimeoutException)) {
            return mContext.getString(R.string.error_timeout);
        } else if ((throwable instanceof IOException) && !NetworkUtil.isConnected(mContext)) {
            return mContext.getString(R.string.error_no_internet_connection);
        } else {
            return mContext.getString(R.string.error_unknown);
        }
    }
}
