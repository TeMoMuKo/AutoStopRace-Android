package pl.temomuko.autostoprace.data.model;

import retrofit.HttpException;
import retrofit.Response;

/**
 * Created by szymen on 2016-01-22.
 */
public class ApiError {

    private String mErrorMessage;
    private int mStatus;

    public ApiError(int status, String errorMessage) {
        mStatus = status;
        mErrorMessage = errorMessage;
    }

    public ApiError(Response response) {
        this(response.code(), response.message());
    }

    public ApiError() {
        this(0, "");
    }

    public static ApiError create(Throwable throwable) {
        ApiError response = new ApiError();
        if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            response = new ApiError(httpException.code(), httpException.message());
        }
        return response;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public int getStatus() {
        return mStatus;
    }
}
