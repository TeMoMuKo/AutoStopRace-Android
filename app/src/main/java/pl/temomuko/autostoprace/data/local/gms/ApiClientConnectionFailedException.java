package pl.temomuko.autostoprace.data.local.gms;

import com.google.android.gms.common.ConnectionResult;

/**
 * Created by Rafał Naniewicz on 09.02.2016.
 */
public class ApiClientConnectionFailedException extends Exception {

    public ConnectionResult getConnectionResult() {
        return mConnectionResult;
    }

    private ConnectionResult mConnectionResult;

    public ApiClientConnectionFailedException(ConnectionResult connectionResult) {
        mConnectionResult = connectionResult;
    }
}
