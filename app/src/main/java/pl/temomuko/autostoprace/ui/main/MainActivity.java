package pl.temomuko.autostoprace.ui.main;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerActivity;
import pl.temomuko.autostoprace.ui.launcher.LauncherActivity;
import pl.temomuko.autostoprace.ui.post.PostActivity;
import pl.temomuko.autostoprace.util.IntentUtil;
import pl.temomuko.autostoprace.util.LogUtil;
import pl.temomuko.autostoprace.util.PermissionUtil;

/**
 * Created by szymen on 2016-01-06.
 */
public class MainActivity extends DrawerActivity implements MainMvpView {

    private static final int CHECK_LOCATION_SETTINGS_REQUEST_CODE = 1;

    @Inject MainPresenter mMainPresenter;
    @Bind(R.id.tv_current_team_locations) TextView mCurrentLocationRecordsTextView;
    @Bind(R.id.horizontal_progress_toolbar) MaterialProgressBar mMaterialProgressBar;
    @Bind(R.id.fab_go_to_post) FloatingActionButton mGoToPostFab;
    private Snackbar mWarningSnackbar;

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
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mMainPresenter.handlePermissionResult(requestCode, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHECK_LOCATION_SETTINGS_REQUEST_CODE) {
            mMainPresenter.handleLocationSettingsDialogResult(resultCode);
        }
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

    @Override
    public void compatRequestFineLocationPermission() {
        PermissionUtil.requestFineLocationPermission(this);
    }

    @Override
    public void showNoFineLocationPermissionWarning() {
        mWarningSnackbar = Snackbar.make(findViewById(R.id.cl_root),
                R.string.warning_no_fine_location_permission,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.settings, v -> IntentUtil.goToAppSettings(this));
        mWarningSnackbar.show();
    }

    @Override
    public void dismissWarning() {
        if (mWarningSnackbar != null && mWarningSnackbar.isShown()) {
            mWarningSnackbar.dismiss();
        }
    }

    @Override
    public void onUserResolvableLocationSettings(Status status) {
        IntentUtil.startGmsStatusForResolution(this,status,CHECK_LOCATION_SETTINGS_REQUEST_CODE);
    }

    @Override
    public void showLocationSettingsWarning() {
        mWarningSnackbar = Snackbar.make(findViewById(R.id.cl_root),
                R.string.warning_inadequate_location_settings,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.change_settings, v -> mMainPresenter.checkLocationSettings());
        mWarningSnackbar.show();
    }

    @Override
    public void onGmsConnectionResultResolutionRequired(ConnectionResult connectionResult) {
        IntentUtil.startGmsConnectionResultForResolution(this,connectionResult,-1);
    }

    @Override
    public void onGmsConnectionResultNoResolution(int errorCode) {
        GoogleApiAvailability.getInstance().getErrorDialog(this,errorCode,0).show();
    }
}
