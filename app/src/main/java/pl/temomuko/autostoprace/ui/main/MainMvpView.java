package pl.temomuko.autostoprace.ui.main;

import java.util.List;

import pl.temomuko.autostoprace.data.model.Location;
import pl.temomuko.autostoprace.data.model.User;
import pl.temomuko.autostoprace.ui.base.content.ContentMvpView;

/**
 * Created by szymen on 2016-01-09.
 */
public interface MainMvpView extends ContentMvpView {

    void updateLocationsList(List<Location> locations);

    void showEmptyInfo();

    void showUser(User user);

    void startLauncherActivity();

    void showLogoutMessage();

    void startPostActivity();
}
