package pl.temomuko.autostoprace.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by szymen on 2016-01-22.
 */
public class ApiErrorResponse {

    @SerializedName("error") private String mErrorMessage;
    @SerializedName("status") private int mStatus;

    public ApiErrorResponse(int status, String errorMessage) {
        mStatus = status;
        mErrorMessage = errorMessage;
    }

    public ApiErrorResponse(String errorMessage) {
        mErrorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public int getStatus() {
        return mStatus;
    }
}
