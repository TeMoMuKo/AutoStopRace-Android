package pl.temomuko.autostoprace.ui.post;

import android.net.Uri;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;

import pl.temomuko.autostoprace.ui.base.MvpView;

/**
 * Created by Szymon Kozak on 2016-01-30.
 */
public interface PostMvpView extends MvpView {

    void closeActivityWithSuccessCode();

    void showSuccessInfo();

    void updateCurrentLocation(double latitude, double longitude, String address);

    void updateCurrentLocation(double latitude, double longitude);

    void clearCurrentLocation();

    void compatRequestFineLocationPermission();

    void onUserResolvableLocationSettings(Status status);

    void onGmsConnectionResultResolutionRequired(ConnectionResult connectionResult);

    void onGmsConnectionResultNoResolution(int errorCode);

    void finishWithInadequateSettingsWarning();

    void showNoLocationEstablishedError();

    void updateAccuracyInfo(float accuracy);

    void setPhoto(Uri uri);

    void clearPhoto();
}
