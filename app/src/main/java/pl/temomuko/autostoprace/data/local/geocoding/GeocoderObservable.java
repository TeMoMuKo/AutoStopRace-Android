package pl.temomuko.autostoprace.data.local.geocoding;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;

import pl.temomuko.autostoprace.util.LogUtil;
import pl.temomuko.autostoprace.util.NetworkUtil;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Rafał Naniewicz on 20.02.2016.
 */
public class GeocoderObservable implements Observable.OnSubscribe<Address> {

    private final Context mContext;
    private final double mLatitude;
    private final double mLongitude;

    public static Observable<Address> create(Context context, double latitude, double longitude) {
        return Observable.create(new GeocoderObservable(context, latitude, longitude));
    }

    private GeocoderObservable(Context context, double latitude, double longitude) {
        mContext = context;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    @Override
    public void call(Subscriber<? super Address> subscriber) {
        if (!subscriber.isUnsubscribed()) {
            try {
                if (!Geocoder.isPresent()) {
                    throw new GeocoderNotPresentException();
                }
                if (!NetworkUtil.isConnected(mContext)) {
                    throw new IOException("No internet connection");
                }
                Geocoder geocoder = new Geocoder(mContext);
                List<Address> addresses = geocoder.getFromLocation(mLatitude, mLongitude, 1);
                if (addresses == null || addresses.isEmpty()) {
                    throw new AddressNotFoundException();
                }
                subscriber.onNext(addresses.get(0));
                subscriber.onCompleted();
            } catch (GeocoderNotPresentException | AddressNotFoundException | IOException e) {
                subscriber.onError(e);
            }
        }
    }
}
