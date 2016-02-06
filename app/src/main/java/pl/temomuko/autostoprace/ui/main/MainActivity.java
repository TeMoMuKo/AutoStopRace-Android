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
import pl.temomuko.autostoprace.ui.base.drawer.DrawerActivity;
import pl.temomuko.autostoprace.ui.launcher.LauncherActivity;
import pl.temomuko.autostoprace.ui.post.PostActivity;

/**
 * Created by szymen on 2016-01-06.
 */
public class MainActivity extends DrawerActivity implements MainMvpView {

    @Inject MainPresenter mMainPresenter;
    @Bind(R.id.tv_current_team_locations) TextView mCurrentLocationsTextView;
    @Bind(R.id.btn_go_to_post) Button mGoToPostButton;
    private String TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActivityComponent().inject(this);
        mMainPresenter.attachView(this);
        mMainPresenter.checkAuth();
        if (mMainPresenter.isAuthorized()) {
            mMainPresenter.loadLocationsFromDatabase();
            mMainPresenter.loadLocationsFromServer();
            mMainPresenter.setupUserInfoInDrawer();
        }
        setupToolbarWithToggle();
        setListeners();
    }

    @Override
    protected void onDestroy() {
        mMainPresenter.detachView();
        super.onDestroy();
    }

    private void setListeners() {
        mGoToPostButton.setOnClickListener(v -> mMainPresenter.goToPostLocation());
    }

    @Override
    public void updateLocationsList(List<Location> locations) {
        mCurrentLocationsTextView.setText(locations.toString());
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setProgress(boolean status) {
        //TODO
    }

    @Override
    public void showEmptyInfo() {
        mCurrentLocationsTextView.setText(R.string.msg_empty_locations_list);
    }

    @Override
    public void showLogoutMessage() {
        Toast.makeText(this, R.string.msg_logout_success, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startLauncherActivity() {
        Intent intent = new Intent(this, LauncherActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void startLoginActivity() {
        Intent intent = new Intent(this, LauncherActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void startPostActivity() {
        Intent intent = new Intent(this, PostActivity.class);
        startActivity(intent);
    }

    @Override
    public void showSessionExpiredError() {
        Toast.makeText(this, R.string.error_session_expired, Toast.LENGTH_SHORT).show();
    }
}
