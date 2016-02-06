package pl.temomuko.autostoprace.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import javax.inject.Inject;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.local.PrefsHelper;
import pl.temomuko.autostoprace.ui.launcher.LauncherActivity;

/**
 * Created by szymen on 2016-02-05.
 */
public class SettingsFragment extends PreferenceFragment implements SettingsMvpView {

    @Inject SettingsPresenter mSettingsPresenter;
    private Preference mLogoutPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        mLogoutPreference = findPreference(PrefsHelper.PREF_LOGOUT);
        ((SettingsActivity) getActivity()).getActivityComponent().inject(this);
        mSettingsPresenter.attachView(this);
        mSettingsPresenter.setupLogoutPreference();
        setListeners();
    }

    @Override
    public void onDestroy() {
        mSettingsPresenter.detachView();
        super.onDestroy();
    }

    private void setListeners() {
        mLogoutPreference.setOnPreferenceClickListener(preference -> {
            mSettingsPresenter.logout();
            return true;
        });
    }

    public void setupLogoutPreferenceSummary(boolean isAuth, String username) {
        String loggedAsMessage = getActivity().getString(R.string.pref_logout_summary, username);
        String notLoggedMessage = getActivity().getString(R.string.pref_logout_summary_not_logged);
        mLogoutPreference.setSummary(isAuth ? loggedAsMessage : notLoggedMessage);
    }

    public void setupLogoutPreferenceEnabled(boolean state) {
        mLogoutPreference.setEnabled(state);
    }

    @Override
    public void showLogoutMessage() {
        Toast.makeText(getActivity(), R.string.msg_logout_success, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startLauncherActivity() {
        Intent intent = new Intent(getActivity(), LauncherActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}
