package pl.temomuko.autostoprace.ui.settings;

import pl.temomuko.autostoprace.ui.base.MvpView;

/**
 * Created by Szymon Kozak on 2016-02-05.
 */
public interface SettingsMvpView extends MvpView {

    void showLogoutMessage();

    void startLauncherActivity();

    void setupLogoutPreferenceSummary(boolean isAuth, String summary);

    void setupLogoutPreferenceEnabled(boolean state);
}
