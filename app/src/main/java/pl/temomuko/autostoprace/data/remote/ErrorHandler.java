package pl.temomuko.autostoprace.data.remote;

import android.content.Context;
import android.util.Patterns;

import java.io.IOException;
import java.net.SocketTimeoutException;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.injection.AppContext;
import pl.temomuko.autostoprace.util.NetworkUtil;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * Created by Szymon Kozak on 2016-01-27.
 */

@Singleton
public class ErrorHandler {

    public static final String TAG = ErrorHandler.class.getSimpleName();

    private final Context context;
    private final ErrorResponseMessageProvider errorResponseMessageProvider;

    @Inject
    public ErrorHandler(
            @AppContext Context context,
            ErrorResponseMessageProvider errorResponseMessageProvider
    ) {
        this.context = context;
        this.errorResponseMessageProvider = errorResponseMessageProvider;
    }

    public boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //todo create custom rxjava call adapter to handle errors
    public String getMessage(Throwable throwable) {
        if (throwable instanceof HttpException) {
            return getMessageFromHttpCode(((HttpException) throwable).code());
        } else if (throwable instanceof TeamNotFoundException) {
            return getTeamNotFoundMessage();
        } else if (throwable instanceof StandardResponseException) {
            return getMessageFromHttpResponse(((StandardResponseException) throwable).getResponse());
        } else {
            return getMessageFromRetrofitThrowable(throwable);
        }
    }

    private String getTeamNotFoundMessage() {
        return context.getString(R.string.error_team_not_found);
    }

    private String getMessageFromHttpResponse(Response<?> response) {
        String errorMessage = errorResponseMessageProvider.getErrorFromResponseBody(response);
        if (errorMessage == null) {
            return getMessageFromHttpCode(response.code());
        } else {
            return errorMessage;
        }
    }

    private String getMessageFromHttpCode(int httpCode) {
        switch (httpCode) {
            case HttpStatus.NOT_FOUND:
                return context.getString(R.string.error_404);
            case HttpStatus.FORBIDDEN:
                return context.getString(R.string.error_403);
            case HttpStatus.UNAUTHORIZED:
                return context.getString(R.string.error_401);
            case HttpStatus.BAD_REQUEST:
                return context.getString(R.string.error_400);
            case HttpStatus.INTERNAL_SERVER_ERROR:
                return context.getString(R.string.error_500);
            case HttpStatus.BAD_GATEWAY:
                return context.getString(R.string.error_502);
            default:
                return context.getString(R.string.error_unknown);
        }
    }

    private String getMessageFromRetrofitThrowable(Throwable throwable) {
        if ((throwable instanceof SocketTimeoutException)) {
            return context.getString(R.string.error_timeout);
        } else if ((throwable instanceof IOException) && !NetworkUtil.isConnected(context)) {
            return context.getString(R.string.error_no_internet_connection);
        } else {
            throwable.printStackTrace();
            return context.getString(R.string.error_unknown);
        }
    }
}
