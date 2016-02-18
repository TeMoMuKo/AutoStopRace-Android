package pl.temomuko.autostoprace.data.local.gms;

import android.content.Context;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Rafał Naniewicz on 10.02.2016.
 */
public class ApiClientObservable extends GmsBaseObservable<GoogleApiClient> {

    @SafeVarargs
    public static Observable<GoogleApiClient> create(Context context, Api<? extends Api.ApiOptions.NotRequiredOptions>... apis) {
        return Observable.create(new ApiClientObservable(context, apis));
    }

    @SafeVarargs
    private ApiClientObservable(Context context, Api<? extends Api.ApiOptions.NotRequiredOptions>... services) {
        super(context, services);
    }

    @Override
    protected void onApiClientReady(GoogleApiClient apiClient, Subscriber<? super GoogleApiClient> subscriber) {
        if (!subscriber.isUnsubscribed()) {
            subscriber.onNext(apiClient);
            subscriber.onCompleted();
        }
    }
}
