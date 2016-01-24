package pl.temomuko.autostoprace.data.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;

import retrofit.HttpException;

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

    public ApiErrorResponse() {
        this(0, "");
    }

    public static ApiErrorResponse create(Throwable throwable) {
        if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            try {
                return new Gson().fromJson(httpException.response().errorBody().string(), ApiErrorResponse.class);
            } catch (IOException e) {
                return new ApiErrorResponse(httpException.code(), httpException.message());
            }
        }
        return new ApiErrorResponse();
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public int getStatus() {
        return mStatus;
    }
}
