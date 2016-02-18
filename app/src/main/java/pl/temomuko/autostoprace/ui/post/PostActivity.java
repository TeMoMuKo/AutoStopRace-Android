package pl.temomuko.autostoprace.ui.post;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;

import javax.inject.Inject;

import butterknife.Bind;
import de.greenrobot.event.EventBus;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.event.GpsStatusChangeEvent;
import pl.temomuko.autostoprace.ui.base.BaseActivity;
import pl.temomuko.autostoprace.ui.main.MainActivity;
import pl.temomuko.autostoprace.util.LogUtil;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_post_send:
                String message = mMessageEditText.getText().toString().trim();
                mPostPresenter.saveLocation(message);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHECK_LOCATION_SETTINGS_REQUEST_CODE) {
            mIsLocationSettingsStatusForResultCalled = false;
            mPostPresenter.handleLocationSettingsActivityResult(resultCode);
        }
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
    public void startLocationSettingsStatusResolution(Status status) {
        try {
            mIsLocationSettingsStatusForResultCalled = true;
            status.startResolutionForResult(this, CHECK_LOCATION_SETTINGS_REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            LogUtil.e("Intent sender exception", e.getMessage());
        }
    }

    @Override
    public void displayGPSFixAcquired() {
        Toast.makeText(this, R.string.location_acquired, Toast.LENGTH_LONG).show();
    }

    @Override
    public void startConnectionResultResolution(ConnectionResult connectionResult) {
        try {
            connectionResult.startResolutionForResult(this, 0);
        } catch (IntentSender.SendIntentException e) {
            LogUtil.e("Intent sender exception", e.getMessage());
        }
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
    public boolean isLocationSettingsStatusForResultCalled() {
        return mIsLocationSettingsStatusForResultCalled;
    }

    /* Events */

    @SuppressWarnings("unused")
    public void onEventMainThread(GpsStatusChangeEvent event) {
        mPostPresenter.handleGpsStatusChange();
    }
}
