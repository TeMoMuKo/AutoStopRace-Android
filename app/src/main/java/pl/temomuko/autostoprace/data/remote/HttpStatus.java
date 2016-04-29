package pl.temomuko.autostoprace.data.remote;

import retrofit2.Response;
import rx.Observable;

/**
 * Created by Szymon Kozak on 2016-01-27.
 */
public final class HttpStatus {

    private HttpStatus() {
        throw new AssertionError();
    }

    public static final int OK = 200;
    public static final int CREATED = 201;

    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;

    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int BAD_GATEWAY = 502;

    public static <T> Observable<Response<T>> require(Response<T> response, int requiredStatus) {
        return response.code() == requiredStatus ?
                Observable.just(response) :
                Observable.error(new StandardResponseException(response));
    }

    public static <T> Observable<Response<T>> requireOk(Response<T> response) {
        return require(response, HttpStatus.OK);
    }

    public static <T> Observable<Response<T>> requireCreated(Response<T> response) {
        return require(response, HttpStatus.CREATED);
    }
}
