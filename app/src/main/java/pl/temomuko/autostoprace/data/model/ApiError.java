package pl.temomuko.autostoprace.data.model;

import retrofit2.Response;

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

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public int getStatus() {
        return mStatus;
    }
}
