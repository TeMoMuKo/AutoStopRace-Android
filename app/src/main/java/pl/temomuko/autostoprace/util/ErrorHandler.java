package pl.temomuko.autostoprace.util;

import android.content.Context;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.model.ApiError;
import retrofit.Response;

/**
 * Created by szymen on 2016-01-27.
 */

public class ErrorHandler {

    private Context mContext;
    private ApiError mResponse;

    public ErrorHandler(Context context, Throwable throwable) {
        mContext = context;
        mResponse = ApiError.create(throwable);
    }

    public ErrorHandler(Context context, Response<?> response) {
        mContext = context;
        mResponse = new ApiError(response);
    }

    public String getMessage() {
        switch (mResponse.getStatus()) {
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

}
