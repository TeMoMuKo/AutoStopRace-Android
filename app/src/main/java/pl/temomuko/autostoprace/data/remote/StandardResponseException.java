package pl.temomuko.autostoprace.data.remote;

import java.io.IOException;

import retrofit2.Response;

/**
 * Created by szymen on 2016-02-12.
 */
public class StandardResponseException extends IOException {

    Response<?> mResponse;

    public StandardResponseException(Response<?> response) {
        mResponse = response;
    }

    public Response<?> getResponse() {
        return mResponse;
    }
}
