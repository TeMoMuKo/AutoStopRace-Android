package pl.temomuko.autostoprace.data.local.gms;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Arrays;
import java.util.List;

import pl.temomuko.autostoprace.util.LogUtil;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import static com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

/**
 * Created by Rafał Naniewicz on 10.02.2016.
 */
abstract class GmsBaseObservable<T> implements Observable.OnSubscribe<T>, ConnectionCallbacks, OnConnectionFailedListener {

    private final Context mContext;
    private final List<Api<? extends Api.ApiOptions.NotRequiredOptions>> mServices;

    private GoogleApiClient mApiClient;
    private Subscriber<? super T> mSubscriber;

    @SafeVarargs
    protected GmsBaseObservable(Context context, Api<? extends Api.ApiOptions.NotRequiredOptions>... services) {
        mContext = context;
        mServices = Arrays.asList(services);
    }

    @Override
    public void call(Subscriber<? super T> subscriber) {
        mSubscriber = subscriber;
        mApiClient = createGoogleApiClient();
        mApiClient.connect();

        subscriber.add(Subscriptions.create(() -> {
            if (mApiClient.isConnected() || mApiClient.isConnecting()) {
                onUnsubscribe(mApiClient);
                mApiClient.disconnect();
            }
        }));
    }

    private GoogleApiClient createGoogleApiClient() {
        GoogleApiClient.Builder apiClientBuilder = new GoogleApiClient.Builder(mContext)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this);
        for (Api<? extends Api.ApiOptions.NotRequiredOptions> service : mServices) {
            apiClientBuilder.addApi(service);
        }
        return apiClientBuilder.build();
    }

    protected abstract void onApiClientReady(GoogleApiClient googleApi, Subscriber<? super T> subscriber);

    protected void onUnsubscribe(GoogleApiClient apiClient) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        LogUtil.i("GMS", "connected");
        onApiClientReady(mApiClient, mSubscriber);
    }

    @Override
    public void onConnectionSuspended(int i) {
        LogUtil.i("GMS", "suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        LogUtil.e("GMS", "connection failed");
        if (!mSubscriber.isUnsubscribed()) {
            mSubscriber.onError(new ApiClientConnectionFailedException(connectionResult));
        }
    }
}
