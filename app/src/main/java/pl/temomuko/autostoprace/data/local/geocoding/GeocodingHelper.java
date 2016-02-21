package pl.temomuko.autostoprace.data.local.geocoding;

import android.content.Context;
import android.location.Address;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.injection.AppContext;
import pl.temomuko.autostoprace.util.RxUtil;
import rx.Observable;

/**
 * Created by Rafał Naniewicz on 19.02.2016.
 */
@Singleton
public class GeocodingHelper {

    Context mContext;

    @Inject
    public GeocodingHelper(@AppContext Context context) {
        mContext = context;
    }

    public Observable<Address> getFromLocation(double latitude, double longitude) {
        return GeocoderObservable.create(mContext, latitude, longitude)
                .compose(RxUtil.applySchedulers());
    }
}
