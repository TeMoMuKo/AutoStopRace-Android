package pl.temomuko.autostoprace.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.model.Location;
import pl.temomuko.autostoprace.data.model.User;
import pl.temomuko.autostoprace.ui.base.BaseActivity;
import pl.temomuko.autostoprace.ui.launcher.LauncherActivity;

/**
 * Created by szymen on 2016-01-06.
 */
public class MainActivity extends BaseActivity implements MainMvpView {

    @Inject MainPresenter mMainPresenter;
    @Bind(R.id.tv_current_team_locations) TextView mCurrentLocationsTextView;
    @Bind(R.id.tv_current_user) TextView mCurrentUserTextView;
    @Bind(R.id.btn_logout) Button mLogoutButton;
    private String TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActivityComponent().inject(this);
        mMainPresenter.attachView(this);
        mMainPresenter.checkAuth();
        mMainPresenter.loadLocationsFromDatabase();
        mMainPresenter.loadLocationsFromServer();
        mMainPresenter.setupUserInfo();
        setListeners();
    }

    @Override
    protected void onDestroy() {
        mMainPresenter.detachView();
        super.onDestroy();
    }

    private void setListeners() {
        mLogoutButton.setOnClickListener(v -> mMainPresenter.logout());
    }

    @Override
    public void goToLauncherActivity() {
        Intent intent = new Intent(this, LauncherActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void updateLocationsList(List<Location> locations) {
        mCurrentLocationsTextView.setText(locations.toString());
    }

    @Override
    public void showApiError(String message) {
        mCurrentLocationsTextView.setText(message);
    }

    @Override
    public void showEmptyInfo() {
        mCurrentLocationsTextView.setText(R.string.msg_empty_locations_list);
    }

    @Override
    public void showUser(User user) {
        mCurrentUserTextView.setText(user.getFirstName());
    }

    @Override
    public void showLogoutMessage() {
        Toast.makeText(this, R.string.msg_logout_success, Toast.LENGTH_SHORT).show();
    }
}
