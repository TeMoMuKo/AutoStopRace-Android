package pl.temomuko.autostoprace.ui.post;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.BindView;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.Event;
import pl.temomuko.autostoprace.data.local.photo.ImageSourceType;
import pl.temomuko.autostoprace.ui.base.BaseActivity;
import pl.temomuko.autostoprace.ui.main.Shortcuts;
import pl.temomuko.autostoprace.util.CoordsUtil;
import pl.temomuko.autostoprace.util.IntentUtil;
import pl.temomuko.autostoprace.util.LocationSettingsUtil;
import pl.temomuko.autostoprace.util.PermissionUtil;

/**
 * Created by Szymon Kozak on 2016-01-30.
 */
public class PostActivity extends BaseActivity implements PostMvpView, PhotoSourceChooserBottomSheet.OnPhotoSourceSelectedListener {

    private static final int REQUEST_CODE_FINE_LOCATION_PERMISSION = 0;
    private static final int REQUEST_CODE_CHECK_LOCATION_SETTINGS = 1;
    private static final int REQUEST_CODE_UNHANDLED = -1;

    private static final String BUNDLE_CURRENT_PHOTO_URI = "bundle_current_photo_uri";

    @Inject PostPresenter mPostPresenter;

    @BindView(R.id.container_layout) LinearLayout containerLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.et_message) EditText mMessageEditText;
    @BindView(R.id.tv_current_location_cords) TextView mCurrentLocationCordsTextView;
    @BindView(R.id.tv_current_location_adress) TextView mCurrentLocationAddressTextView;
    @BindView(R.id.tv_accuracy) TextView mCurrentAccuracyTextView;
    @BindView(R.id.photo_panel_layout) RelativeLayout photoPanelLayout;
    @BindView(R.id.photo_image_view) ImageView photoImageView;
    @BindView(R.id.remove_photo_btn) ImageView removePhotoButton;

    private Uri currentPhotoUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        getActivityComponent().inject(this);
        mPostPresenter.attachView(this);
        setupToolbarWithBack();
        reportShortcutUsage(Shortcuts.POST_LOCATION);
        removePhotoButton.setOnClickListener(v -> mPostPresenter.removePhoto());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Uri photoFromSavedInstance = savedInstanceState.getParcelable(BUNDLE_CURRENT_PHOTO_URI);
        if (photoFromSavedInstance != null) {
            setPhoto(photoFromSavedInstance);
        }

        mPostPresenter.checkForUnreceivedPhoto();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentPhotoUri != null) {
            outState.putParcelable(BUNDLE_CURRENT_PHOTO_URI, currentPhotoUri);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPostPresenter.startLocationService();
        mPostPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPostPresenter.stopLocationService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        mPostPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_FINE_LOCATION_PERMISSION) {
            mPostPresenter.handleLocationPermissionResult(PermissionUtil.wasAllGranted(grantResults));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CHECK_LOCATION_SETTINGS) {
            mPostPresenter.setIsLocationSettingsStatusForResultCalled(false);
            resultCode = LocationSettingsUtil.getApiDependentResultCode(resultCode, data);
            mPostPresenter.handleLocationSettingsDialogResult(resultCode);
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
            case R.id.action_add_photo:
                showPhotoSourceChooserBottomSheet();
                return true;
            case R.id.action_post_send:
                String message = mMessageEditText.getText().toString().trim();
                mPostPresenter.tryToSaveLocation(message, currentPhotoUri);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupToolbarWithBack() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /* MVP View methods */

    @Override
    public void closeActivityWithSuccessCode() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void showSuccessInfo() {
        Toast.makeText(this, R.string.saved_location, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateCurrentLocation(double latitude, double longitude, String address) {
        mCurrentLocationAddressTextView.setText(address);
        String cords = CoordsUtil.getDmsTextFromDecimalDegrees(latitude, longitude);
        mCurrentLocationCordsTextView.setVisibility(View.VISIBLE);
        mCurrentLocationCordsTextView.setText(cords);
    }

    @Override
    public void updateCurrentLocation(double latitude, double longitude) {
        String cords = CoordsUtil.getDmsTextFromDecimalDegrees(latitude, longitude);
        mCurrentLocationCordsTextView.setVisibility(View.GONE);
        mCurrentLocationAddressTextView.setText(cords);
    }

    @Override
    public void clearCurrentLocation() {
        mCurrentLocationAddressTextView.setText(getString(R.string.msg_establishing_approximate_location));
        mCurrentLocationCordsTextView.setVisibility(View.GONE);
    }

    @Override
    public void onUserResolvableLocationSettings(Status status) {
        IntentUtil.startGmsStatusForResolution(this, status, REQUEST_CODE_CHECK_LOCATION_SETTINGS);
        mPostPresenter.setIsLocationSettingsStatusForResultCalled(true);
    }

    @Override
    public void onGmsConnectionResultResolutionRequired(ConnectionResult connectionResult) {
        IntentUtil.startGmsConnectionResultForResolution(this, connectionResult, REQUEST_CODE_UNHANDLED);
    }

    @Override
    public void onGmsConnectionResultNoResolution(int errorCode) {
        GoogleApiAvailability.getInstance().getErrorDialog(this, errorCode, REQUEST_CODE_UNHANDLED).show();
    }

    @Override
    public void compatRequestFineLocationPermission() {
        PermissionUtil.requestFineLocationPermission(this, REQUEST_CODE_FINE_LOCATION_PERMISSION);
    }

    @Override
    public void finishWithInadequateSettingsWarning() {
        Toast.makeText(this, R.string.warning_inadequate_location_settings, Toast.LENGTH_LONG).show();
        closeActivityWithSuccessCode();
    }

    @Override
    public void showNoLocationEstablishedError() {
        Toast.makeText(this, R.string.msg_wait_for_established_location, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateAccuracyInfo(float accuracy) {
        mCurrentAccuracyTextView.setText(getString(R.string.accuracy_label, Math.round(accuracy)));
    }

    @Override
    public void setPhoto(Uri uri) {
        currentPhotoUri = uri;
        photoPanelLayout.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(uri)
                .into(photoImageView);
    }

    @Override
    public void clearPhoto() {
        currentPhotoUri = null;
        photoPanelLayout.setVisibility(View.GONE);
    }

    @Override
    public void showErrorWhileTakingPhoto() {
        Snackbar.make(containerLayout, R.string.error_while_taking_photo, Snackbar.LENGTH_LONG).show();
    }

    private void showPhotoSourceChooserBottomSheet() {
        PhotoSourceChooserBottomSheet photoSourceChooserBottomSheet =
                PhotoSourceChooserBottomSheet.newInstance();
        photoSourceChooserBottomSheet.show(getSupportFragmentManager(), PhotoSourceChooserBottomSheet.TAG);
    }

    /* Events */

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGpsStatusChangeEvent(Event.GpsStatusChanged event) {
        mPostPresenter.handleLocationSettingsStatusChange();
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAirplaneModeStatusChange(Event.AirplaneModeStatusChanged event) {
        mPostPresenter.handleLocationSettingsStatusChange();
    }

    @Override
    public void onImageSourceSelected(ImageSourceType imageSourceType) {
        mPostPresenter.requestPhoto(imageSourceType);
    }
}