package pl.temomuko.autostoprace.data.remote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Patterns;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.injection.AppContext;
import pl.temomuko.autostoprace.util.LogUtil;
import pl.temomuko.autostoprace.util.NetworkUtil;
import retrofit2.Response;

/**
 * Created by Szymon Kozak on 2016-01-27.
 */

@Singleton
public class ErrorHandler {

    public static final String TAG = ErrorHandler.class.getSimpleName();
    private final Context mContext;
    private final ApiManager mApiManager;

    @Inject
    public ErrorHandler(@AppContext Context context, ApiManager apiManager) {
        mContext = context;
        mApiManager = apiManager;
    }

    public boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public String getTeamNotFoundMessage() {
        return mContext.getString(R.string.error_team_not_found);
    }

    public String getMessage(Throwable throwable) {
        if (throwable instanceof TeamNotFoundException) {
            return getTeamNotFoundMessage();
        } else if (throwable instanceof StandardResponseException) {
            return getMessageFromHttpResponse(((StandardResponseException) throwable).getResponse());
        } else {
            return getMessageFromRetrofitThrowable(throwable);
        }
    }

    private String getMessageFromHttpResponse(Response<?> response) {
        List<String> errorsFromResponseBody = getErrorsFromResponseBody(response);
        if (errorsFromResponseBody.isEmpty()) {
            return getStandardMessageForApiError(response);
        } else {
            return errorsFromResponseBody.get(0);
        }
    }

    @NonNull
    private String getStandardMessageForApiError(Response response) {
        switch (response.code()) {
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

    private List<String> getErrorsFromResponseBody(Response response) {
        try {
            return mApiManager.getErrorResponseConverter()
                    .convert(response.errorBody())
                    .getErrors();
        } catch (IOException e) {
            LogUtil.i(TAG, "It isn't ErrorResponse object.");
        }
        return new ArrayList<>();
    }

    private String getMessageFromRetrofitThrowable(Throwable throwable) {
        if ((throwable instanceof SocketTimeoutException)) {
            return mContext.getString(R.string.error_timeout);
        } else if ((throwable instanceof IOException) && !NetworkUtil.isConnected(mContext)) {
            return mContext.getString(R.string.error_no_internet_connection);
        } else {
            throwable.printStackTrace();
            return mContext.getString(R.string.error_unknown);
        }
    }
}
