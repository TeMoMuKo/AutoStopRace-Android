package pl.temomuko.autostoprace.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerActivity;
import pl.temomuko.autostoprace.ui.launcher.LauncherActivity;
import pl.temomuko.autostoprace.ui.post.PostActivity;

/**
 * Created by szymen on 2016-01-06.
 */
public class MainActivity extends DrawerActivity implements MainMvpView {

    @Inject MainPresenter mMainPresenter;
    @Bind(R.id.tv_current_team_locations) TextView mCurrentLocationRecordsTextView;
    @Bind(R.id.horizontal_progress_toolbar) MaterialProgressBar mMaterialProgressBar;
    @Bind(R.id.fab_go_to_post) FloatingActionButton mGoToPostFab;
    private Snackbar mNoFineLocationPermissionSnackbar;

    private String TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActivityComponent().inject(this);
        mMainPresenter.attachView(this);
        mMainPresenter.checkAuth();
        if (mMainPresenter.isAuthorized()) {
            mMainPresenter.loadLocations();
            mMainPresenter.setupUserInfoInDrawer();
        }
        setupToolbarWithToggle();
        setListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mMainPresenter.handlePermissionResult(requestCode, grantResults);
    }

    @Override
    protected void onDestroy() {
        mMainPresenter.detachView();
        super.onDestroy();
    }

    private void setListeners() {
        mGoToPostFab.setOnClickListener(v -> mMainPresenter.goToPostLocation());
    }

    /* MVP View methods */

    @Override
    public boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void compatRequestFineLocationPermission(int requestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                requestCode);
    }

    @Override
    public void showNoFineLocationPermissionSnackbar() {
        mNoFineLocationPermissionSnackbar = Snackbar.make(findViewById(R.id.cl_root),
                R.string.warning_no_fine_location_permission,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.settings, v -> goToAppSettings());
        mNoFineLocationPermissionSnackbar.show();
    }

    private void goToAppSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .addCategory(Intent.CATEGORY_DEFAULT)
                .setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @Override
    public void dismissNoFineLocationPermissionSnackbar() {
        if (mNoFineLocationPermissionSnackbar != null && mNoFineLocationPermissionSnackbar.isShown()) {
            mNoFineLocationPermissionSnackbar.dismiss();
        }
    }

    @Override
    public void updateLocationRecordsList(List<LocationRecord> locationRecords) {
        mCurrentLocationRecordsTextView.setText(locationRecords.toString());
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setProgress(boolean status) {
        mMaterialProgressBar.setVisibility(status ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void showEmptyInfo() {
        mCurrentLocationRecordsTextView.setText(R.string.msg_empty_location_records_list);
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
