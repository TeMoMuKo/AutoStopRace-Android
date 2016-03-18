package pl.temomuko.autostoprace.data.local.geocoding;

import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.support.annotation.NonNull;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.injection.AppContext;
import pl.temomuko.autostoprace.util.LogUtil;
import pl.temomuko.autostoprace.util.rx.RxUtil;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Rafał Naniewicz on 19.02.2016.
 */
@Singleton
public class GeocodingHelper {

    private static final String TAG = "GeocodingHelper";

    private Context mContext;

    @Inject
    public GeocodingHelper(@AppContext Context context) {
        mContext = context;
    }

    public Observable<Address> getAddressFromLocation(@NonNull Location location) {
        return GeocoderObservable.create(mContext, location.getLatitude(), location.getLongitude())
                .timeout(Constants.GEO_CODING_TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS)
                .onErrorResumeNext(throwable -> {
                    LogUtil.i(TAG, throwable.toString() + ", returning basic address instead");
                    return Observable.just(getBasicAddress(location));
                })
                .subscribeOn(Schedulers.io());
    }

    private Address getBasicAddress(Location location) {
        Address resultAddress = new Address(Locale.getDefault());
        resultAddress.setLatitude(location.getLatitude());
        resultAddress.setLongitude(location.getLongitude());
        return resultAddress;
    }
}
