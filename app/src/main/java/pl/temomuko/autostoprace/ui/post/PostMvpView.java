package pl.temomuko.autostoprace.ui.post;

import com.google.android.gms.common.api.Status;

import pl.temomuko.autostoprace.ui.base.MvpView;

/**
 * Created by szymen on 2016-01-30.
 */
public interface PostMvpView extends MvpView {

    void startMainActivity();

    void showSuccessInfo();

    void updateCurrentLocationCords(double latitude, double longitude);

    void updateCurrentLocationAddress(String adress);

    void startStatusResolution(Status status, int checkSettingsRequestCode);

    void showLocationSettingsWarning();

    void displayGPSFixFound();
}
