package pl.temomuko.autostoprace.ui.post;

import android.app.Activity;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.ui.base.BasePresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.RxUtil;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by szymen on 2016-01-30.
 */
public class PostPresenter extends BasePresenter<PostMvpView> {

    private static final int CHECK_SETTINGS_REQUEST_CODE = 1;
    private static final int UPDATE_INTERVAL_MILLISECONDS = 5000;
    private static final int FASTEST_UPDATE_INTERVAL_MILLISECONDS = UPDATE_INTERVAL_MILLISECONDS / 2;
    private static final int LOCATION_ACCURACY = LocationRequest.PRIORITY_HIGH_ACCURACY;

    private DataManager mDataManager;
    private ErrorHandler mErrorHandler;
    private CompositeSubscription mSubscriptions;
    private Subscription mLocationSubscription;

    private Location mLatestLocation;
    private LocationRequest mLocationRequest;

    private final static String TAG = "PostPresenter";

    @Inject
    public PostPresenter(DataManager dataManager, ErrorHandler errorHandler) {
        mDataManager = dataManager;
        mErrorHandler = errorHandler;
        mLocationRequest = new LocationRequest()
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MILLISECONDS)
                .setInterval(UPDATE_INTERVAL_MILLISECONDS)
                .setPriority(LOCATION_ACCURACY);
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(PostMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        if (mSubscriptions != null) mSubscriptions.unsubscribe();
        super.detachView();
    }

    public void saveLocation(String message) {
        //todo tmp to prevent nullptr
        double latitude = mLatestLocation != null ? mLatestLocation.getLatitude() : 0;
        double longitude = mLatestLocation != null ? mLatestLocation.getLongitude() : 0;
        LocationRecord locationRecordToSend = new LocationRecord(latitude, longitude, message);
        mSubscriptions.add(mDataManager.saveUnsentLocationRecordToDatabase(locationRecordToSend)
                .compose(RxUtil.applySchedulers())
                .subscribe());
        getMvpView().showSuccessInfo();
        getMvpView().startMainActivity();
    }

    public void setupCurrentLocation() {
        getMvpView().updateCurrentLocationAddress("ul. Sezamkowa 12, Wrocław, Polska");
        getMvpView().updateCurrentLocationCords(51.12345, 21.12345);
    }

    public void startLocationService() {
        mSubscriptions.add(mDataManager.checkLocationSettings(mLocationRequest)
                .subscribe(this::handleLocationSettingsResult));
    }

    public void stopLocationService() {
        if (mLocationSubscription != null) {
            mLocationSubscription.unsubscribe();
        }
    }

    private void handleLocationSettingsResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                getMvpView().startStatusResolution(status, CHECK_SETTINGS_REQUEST_CODE);
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    private void startLocationUpdates() {
        mLocationSubscription = mDataManager.getDeviceLocation(mLocationRequest)
                .subscribe(this::handleLocationUpdate);
    }

    private void handleLocationUpdate(Location location) {
        if (mLatestLocation == null) {
            // TODO: 16.02.2016 temporary
            getMvpView().displayGPSFixFound();
        }
        mLatestLocation = location;
        getMvpView().updateCurrentLocationCords(mLatestLocation.getLatitude(), mLatestLocation.getLongitude());
        //// TODO: 16.02.2016 implement reverse geo
        getMvpView().updateCurrentLocationAddress("Somewhere");
    }

    public void handleActivityResult(int requestCode, int resultCode) {
        switch (requestCode) {
            case CHECK_SETTINGS_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        getMvpView().showLocationSettingsWarning();
                        break;
                }
        }
    }
}
