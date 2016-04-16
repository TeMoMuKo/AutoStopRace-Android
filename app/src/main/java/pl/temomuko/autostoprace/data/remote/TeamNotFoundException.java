package pl.temomuko.autostoprace.data.remote;

import android.support.annotation.NonNull;

import retrofit2.Response;

/**
 * Created by Szymon Kozak on 2016-02-12.
 */
public class TeamNotFoundException extends StandardResponseException {

    public TeamNotFoundException(@NonNull Response<?> response) {
        super(response);
    }
}
