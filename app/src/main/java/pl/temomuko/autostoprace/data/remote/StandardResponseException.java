package pl.temomuko.autostoprace.data.remote;

import android.support.annotation.NonNull;

import java.io.IOException;

import retrofit2.Response;

/**
 * Created by Szymon Kozak on 2016-02-12.
 */
public class StandardResponseException extends IOException {

    @NonNull Response<?> mResponse;

    public StandardResponseException(@NonNull Response<?> response) {
        mResponse = response;
    }

    @NonNull
    public Response<?> getResponse() {
        return mResponse;
    }
}
