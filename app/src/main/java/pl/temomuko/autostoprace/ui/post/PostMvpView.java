package pl.temomuko.autostoprace.ui.post;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;

import pl.temomuko.autostoprace.ui.base.MvpView;

/**
 * Created by Szymon Kozak on 2016-01-30.
 */
public interface PostMvpView extends MvpView {

    void startMainActivity();

    void showSuccessInfo();

    void updateCurrentLocationCords(double latitude, double longitude);

    void updateCurrentLocationAddress(String address);

    void compatRequestFineLocationPermission();

    void onUserResolvableLocationSettings(Status status);

    void displayGPSFixAcquired();

    void onGmsConnectionResultResolutionRequired(ConnectionResult connectionResult);

    void onGmsConnectionResultNoResolution(int errorCode);

    void finishWithInadequateSettingsWarning();

    void showNoLocationEstablishedError();

    void setSearchingForApproximateAddress();
}
