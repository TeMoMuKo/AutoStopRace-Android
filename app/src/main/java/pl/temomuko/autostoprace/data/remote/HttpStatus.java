package pl.temomuko.autostoprace.data.remote;

/**
 * Created by szymen on 2016-01-27.
 */
public final class HttpStatus {

    private HttpStatus() {
        throw new AssertionError();
    }

    public final static int OK = 200;
    public final static int CREATED = 201;

    public final static int BAD_REQUEST = 400;
    public final static int UNAUTHORIZED = 401;
    public final static int FORBIDDEN = 403;
    public final static int NOT_FOUND = 404;

    public final static int INTERNAL_SERVER_ERROR = 500;
    public final static int BAD_GATEWAY = 502;
}
