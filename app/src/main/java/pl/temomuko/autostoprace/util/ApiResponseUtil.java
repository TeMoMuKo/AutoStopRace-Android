package pl.temomuko.autostoprace.util;

import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.model.ApiErrorResponse;
import retrofit.HttpException;

/**
 * Created by szymen on 2016-01-22.
 */
public class ApiResponseUtil {

    public static ApiErrorResponse getErrorResponse(Context context, Throwable throwable) {
        if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            try {
                return new Gson().fromJson(httpException.response().errorBody().string(), ApiErrorResponse.class);
            } catch (IOException e) {
                return new ApiErrorResponse(httpException.code(), httpException.message());
            }
        }
        return new ApiErrorResponse(context.getString(R.string.error_unknown));
    }
}
