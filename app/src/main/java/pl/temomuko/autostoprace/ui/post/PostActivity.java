package pl.temomuko.autostoprace.ui.post;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.Bind;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.event.GpsStatusChangeEvent;
import pl.temomuko.autostoprace.ui.base.BaseActivity;
import pl.temomuko.autostoprace.ui.main.MainActivity;
import pl.temomuko.autostoprace.util.IntentUtil;
import pl.temomuko.autostoprace.util.PermissionUtil;

/**
 * Created by szymen on 2016-01-30.
 */
public class PostActivity extends BaseActivity implements PostMvpView {

    private static final int CHECK_LOCATION_SETTINGS_REQUEST_CODE = 1;

    @Inject PostPresenter mPostPresenter;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.et_message) EditText mMessageEditText;
    @Bind(R.id.tv_current_location_cords) TextView mCurrentLocationCordsTextView;
    @Bind(R.id.tv_current_location_adress) TextView mCurrentLocationAddressTextView;
    private boolean mIsLocationSettingsStatusForResultCalled = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        getActivityComponent().inject(this);
        mPostPresenter.attachView(this);
        setupToolbarWithBack();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPostPresenter.startLocationService();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mPostPresenter.handleLocationPermissionResult(requestCode, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHECK_LOCATION_SETTINGS_REQUEST_CODE) {
            mIsLocationSettingsStatusForResultCalled = false;
            mPostPresenter.handleLocationSettingsDialogResult(resultCode);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPostPresenter.stopLocationService();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        mPostPresenter.detachView();
        super.onDestroy();
    }

    private void setupToolbarWithBack() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_post_send:
                String message = mMessageEditText.getText().toString().trim();
                mPostPresenter.saveLocation(message);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* MVP View methods */

    @Override
    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void showSuccessInfo() {
        Toast.makeText(this, R.string.saved_location, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateCurrentLocationCords(double latitude, double longitude) {
        String cords = latitude + ", " + longitude;
        mCurrentLocationCordsTextView.setText(cords);
    }

    @Override
    public void updateCurrentLocationAddress(String address) {
        mCurrentLocationAddressTextView.setText(address);
    }

    @Override
    public void onUserResolvableLocationSettings(Status status) {
        IntentUtil.startGmsStatusForResolution(this, status, CHECK_LOCATION_SETTINGS_REQUEST_CODE);
        mIsLocationSettingsStatusForResultCalled = true;
    }

    @Override
    public void displayGPSFixAcquired() {
        Toast.makeText(this, R.string.location_acquired, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGmsConnectionResultResolutionRequired(ConnectionResult connectionResult) {
        IntentUtil.startGmsConnectionResultForResolution(this, connectionResult, -1);
    }

    @Override
    public void onGmsConnectionResultNoResolution(int errorCode) {
        GoogleApiAvailability.getInstance().getErrorDialog(this, errorCode, 0).show();
    }

    @Override
    public void compatRequestFineLocationPermission() {
        PermissionUtil.requestFineLocationPermission(this);
    }

    @Override
    public void finishWithInadequateSettingsWarning() {
        Toast.makeText(this, R.string.warning_inadequate_location_settings, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public boolean isLocationSettingsStatusDialogCalled() {
        return mIsLocationSettingsStatusForResultCalled;
    }

    @Override
    public void showNoLocationEstabilishedError() {
        Toast.makeText(this, R.string.msg_wait_for_estabilished_location, Toast.LENGTH_SHORT).show();
    }

    /* Events */

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGpsStatusChangeEvent(GpsStatusChangeEvent event) {
        mPostPresenter.handleGpsStatusChange();
    }
}
