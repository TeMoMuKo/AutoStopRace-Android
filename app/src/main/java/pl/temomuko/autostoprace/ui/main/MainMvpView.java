package pl.temomuko.autostoprace.ui.main;

import java.util.List;

import pl.temomuko.autostoprace.data.model.Location;
import pl.temomuko.autostoprace.ui.base.MvpView;

/**
 * Created by szymen on 2016-01-09.
 */
public interface MainMvpView extends MvpView {

    void updateLocationsList(List<Location> locations);

    void showError(Throwable throwable);
}
