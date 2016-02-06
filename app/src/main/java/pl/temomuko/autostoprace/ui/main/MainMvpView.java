package pl.temomuko.autostoprace.ui.main;

import java.util.List;

import pl.temomuko.autostoprace.data.model.Location;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerMvpView;

/**
 * Created by szymen on 2016-01-09.
 */
public interface MainMvpView extends DrawerMvpView {

    void updateLocationsList(List<Location> locations);

    void showEmptyInfo();

    void startLauncherActivity();

    void startPostActivity();

    void startLoginActivity();

    void showError(String message);

    void setProgress(boolean state);

    void showSessionExpiredError();
}
