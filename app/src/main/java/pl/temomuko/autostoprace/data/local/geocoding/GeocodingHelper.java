package pl.temomuko.autostoprace.data.local.geocoding;

import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.support.annotation.NonNull;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.injection.AppContext;
import pl.temomuko.autostoprace.util.LogUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rafał Naniewicz on 19.02.2016.
 */
@Singleton
public class GeoCodingHelper {

    private static final int TIMEOUT_IN_SECONDS = 5;
    private static final String TAG = "GeoCodingHelper";

    Context mContext;

    @Inject
    public GeoCodingHelper(@AppContext Context context) {
        mContext = context;
    }

    public Observable<Address> getAddressFromLocation(@NonNull Location location) {
        return GeocoderObservable.create(mContext, location.getLatitude(), location.getLongitude())
                .timeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .onErrorResumeNext(throwable -> {
                    LogUtil.i(TAG, throwable.toString() + ", returning basic address instead");
                    return Observable.just(getBasicAddress(location));
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Address getBasicAddress(Location location) {
        Address resultAddress = new Address(Locale.getDefault());
        resultAddress.setLatitude(location.getLatitude());
        resultAddress.setLongitude(location.getLongitude());
        return resultAddress;
    }
}
