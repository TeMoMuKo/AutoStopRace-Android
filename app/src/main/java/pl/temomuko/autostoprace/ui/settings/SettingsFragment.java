package pl.temomuko.autostoprace.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import javax.inject.Inject;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.local.PrefsHelper;
import pl.temomuko.autostoprace.ui.base.BaseActivity;
import pl.temomuko.autostoprace.ui.launcher.LauncherActivity;
import pl.temomuko.autostoprace.util.DialogFactory;

/**
 * Created by szymen on 2016-02-05.
 */
public class SettingsFragment extends PreferenceFragment implements SettingsMvpView {

    @Inject SettingsPresenter mSettingsPresenter;
    private Preference mLogoutPreference;
    private MaterialDialog mLogoutInfoDialog;
    private static final String BUNDLE_IS_LOGOUT_DIALOG_SHOWN = "bundle_is_progress_logout_shown";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        mLogoutPreference = findPreference(PrefsHelper.PREF_LOGOUT);
        ((BaseActivity) getActivity()).getActivityComponent().inject(this);
        mSettingsPresenter.attachView(this);
        mSettingsPresenter.setupLogoutPreference();
        createLogoutInfoDialog();
        setupLogoutInfoDialog(savedInstanceState);
        setListeners();
    }

    private void createLogoutInfoDialog() {
        mLogoutInfoDialog = DialogFactory.createLogoutInfoDialog(getActivity(), mSettingsPresenter);
    }

    @Override
    public void onDestroy() {
        mSettingsPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        checkLogoutInfoDialog(outState);
    }

    private void setListeners() {
        mLogoutPreference.setOnPreferenceClickListener(preference -> {
            mLogoutInfoDialog.show();
            return true;
        });
    }

    private void setupLogoutInfoDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(BUNDLE_IS_LOGOUT_DIALOG_SHOWN)) {
                mLogoutInfoDialog.show();
            }
        }
    }

    private void checkLogoutInfoDialog(Bundle outState) {
        if (mLogoutInfoDialog != null && mLogoutInfoDialog.isShowing()) {
            mLogoutInfoDialog.dismiss();
            outState.putBoolean(BUNDLE_IS_LOGOUT_DIALOG_SHOWN, true);
        } else {
            outState.putBoolean(BUNDLE_IS_LOGOUT_DIALOG_SHOWN, false);
        }
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
