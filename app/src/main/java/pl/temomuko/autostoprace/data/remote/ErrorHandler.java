package pl.temomuko.autostoprace.data.remote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Patterns;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Provides;
import okhttp3.ResponseBody;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.model.ErrorResponse;
import pl.temomuko.autostoprace.injection.AppContext;
import pl.temomuko.autostoprace.util.LogUtil;
import pl.temomuko.autostoprace.util.NetworkUtil;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Szymon Kozak on 2016-01-27.
 */

@Singleton
public class ErrorHandler {

    public static final String TAG = ErrorHandler.class.getSimpleName();

    private final Context context;
    private final Retrofit retrofit;

    @Inject
    public ErrorHandler(@AppContext Context context, Retrofit retrofit) {
        this.context = context;
        this.retrofit = retrofit;
    }

    public boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
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

    private String getTeamNotFoundMessage() {
        return context.getString(R.string.error_team_not_found);
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

    private List<String> getErrorsFromResponseBody(Response response) {
        List<String> errors = new ArrayList<>();
        try {
            List<String> responseErrors = getErrorResponseConverter()
                    .convert(response.errorBody())
                    .getErrors();
            if (responseErrors != null) {
                errors.addAll(responseErrors);
            }
        } catch (Exception e) {
            LogUtil.i(TAG, "It isn't ErrorResponse object: " + e);
        }
        return errors;
    }

    public Converter<ResponseBody, ErrorResponse> getErrorResponseConverter() {
        return retrofit.responseBodyConverter(ErrorResponse.class, new Annotation[0]);
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
