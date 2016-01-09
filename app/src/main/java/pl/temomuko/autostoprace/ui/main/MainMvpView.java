package pl.temomuko.autostoprace.ui.main;

import pl.temomuko.autostoprace.ui.base.MvpView;

/**
 * Created by szymen on 2016-01-09.
 */
public interface MainMvpView extends MvpView {

    void updateLocationsList();

    void showError();
}
