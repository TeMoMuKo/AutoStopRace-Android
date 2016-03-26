package pl.temomuko.autostoprace.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.event.PostServiceStateChangeEvent;
import pl.temomuko.autostoprace.data.event.SuccessfullyAddedToUnsentTableEvent;
import pl.temomuko.autostoprace.data.event.SuccessfullySentLocationToServerEvent;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.service.PostService;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerActivity;
import pl.temomuko.autostoprace.ui.main.adapter.LocationRecordItem;
import pl.temomuko.autostoprace.ui.main.adapter.LocationRecordsAdapter;
import pl.temomuko.autostoprace.ui.post.PostActivity;
import pl.temomuko.autostoprace.ui.staticdata.launcher.LauncherActivity;
import pl.temomuko.autostoprace.ui.widget.VerticalDividerItemDecoration;
import pl.temomuko.autostoprace.util.AndroidComponentUtil;
import pl.temomuko.autostoprace.util.EventUtil;
import pl.temomuko.autostoprace.util.IntentUtil;
import pl.temomuko.autostoprace.util.LocationSettingsUtil;
import pl.temomuko.autostoprace.util.LogUtil;
import pl.temomuko.autostoprace.util.PermissionUtil;
import pl.temomuko.autostoprace.util.rx.RxUtil;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Szymon Kozak on 2016-01-06.
 */
public class MainActivity extends DrawerActivity implements MainMvpView {

    private static final int CHECK_LOCATION_SETTINGS_REQUEST_CODE = 1;
    private static final int UNHANDLED_REQUEST_CODE = -1;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject MainPresenter mMainPresenter;
    @Inject LocationRecordsAdapter mLocationRecordsAdapter;
    @Inject VerticalDividerItemDecoration mVerticalDividerItemDecoration;
    @Bind(R.id.horizontal_progress_toolbar) MaterialProgressBar mMaterialProgressBar;
    @Bind(R.id.fab_go_to_post) FloatingActionButton mGoToPostFab;
    @Bind(R.id.recycler_view) RecyclerView mRecyclerView;
    @Bind(R.id.tv_empty_info) TextView mEmptyInfoTextView;
    @Bind(R.id.cl_root) CoordinatorLayout mCoordinatorLayoutRoot;
    private Snackbar mWarningSnackbar;
    private LinearLayoutManager mRecyclerViewLinearLayoutManager;

    private CompositeSubscription mSubscriptions;

    private boolean mPostServiceSetProgress;
    private boolean mPresenterSetProgress;
    private boolean mPendingRefresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActivityComponent().inject(this);
        mMainPresenter.attachView(this);
        mSubscriptions = new CompositeSubscription();
        setupToolbarWithToggle();
        setupRecyclerView();
        setListeners();
        if (mMainPresenter.isAuthorized()) {
            mMainPresenter.loadLocations();
            mMainPresenter.setupUserInfoInDrawer();
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
        mMainPresenter.checkAuth();
        startPostService();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mMainPresenter.handlePermissionResult(requestCode, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        resultCode = LocationSettingsUtil.getApiDependentResultCode(resultCode, data);
        if (requestCode == CHECK_LOCATION_SETTINGS_REQUEST_CODE) {
            mMainPresenter.setIsLocationSettingsStatusForResultCalled(false);
            mMainPresenter.handleLocationSettingsDialogResult(resultCode);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        mMainPresenter.detachView();
        if (mSubscriptions != null) mSubscriptions.unsubscribe();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                if (!mPostServiceSetProgress) {
                    mMainPresenter.downloadLocationsFromServer();
                } else {
                    mPendingRefresh = true;
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {
        mRecyclerViewLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mLocationRecordsAdapter);
        mRecyclerView.setLayoutManager(mRecyclerViewLinearLayoutManager);
        mRecyclerView.addItemDecoration(mVerticalDividerItemDecoration);
    }

    private void setListeners() {
        mGoToPostFab.setOnClickListener(v -> mMainPresenter.goToPostLocation());
    }

    /* MVP View methods */

    @Override
    public void setLocationRecordsList(List<LocationRecord> locationRecords) {
        mSubscriptions.add(Observable.from(locationRecords)
                .map(LocationRecordItem::new)
                .toList()
                .map(locationRecordItems -> {
                    Collections.sort(locationRecordItems);
                    return locationRecordItems;
                })
                .compose(RxUtil.applyComputationSchedulers())
                .subscribe(sortedLocationItems -> {
                    mLocationRecordsAdapter.setSortedLocationRecordItems(sortedLocationItems);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mEmptyInfoTextView.setVisibility(View.GONE);
                }));
    }

    @Override
    public void updateLocationRecordsList(List<LocationRecord> locationRecords) {
        if (mLocationRecordsAdapter.getItemCount() != 0) {
            mSubscriptions.add(Observable.from(locationRecords)
                    .map(LocationRecordItem::new)
                    .compose(RxUtil.applyComputationSchedulers())
                    .subscribe(this::updateLocationRecordItemMaintainingScrollPosition));
        } else {
            setLocationRecordsList(locationRecords);
        }
    }

    private void updateLocationRecordItemMaintainingScrollPosition(LocationRecordItem locationRecordItem) {
        int oldFirstVisibleItemIndex = mRecyclerViewLinearLayoutManager.findFirstVisibleItemPosition();
        int oldOffset = mRecyclerViewLinearLayoutManager.getChildAt(0).getTop();
        mLocationRecordsAdapter.updateLocationRecordItem(locationRecordItem);
        mRecyclerViewLinearLayoutManager.scrollToPositionWithOffset(oldFirstVisibleItemIndex, oldOffset);
    }

    @Override
    public void startPostService() {
        if (!AndroidComponentUtil.isServiceRunning(this, PostService.class)) {
            startService(PostService.getStartIntent(this));
        }
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setProgress(boolean state) {
        mPresenterSetProgress = state;
        mMaterialProgressBar.setVisibility(mPresenterSetProgress || mPostServiceSetProgress ?
                View.VISIBLE :
                View.INVISIBLE);
    }

    @Override
    public void showEmptyInfo() {
        mRecyclerView.setVisibility(View.GONE);
        mEmptyInfoTextView.setVisibility(View.VISIBLE);
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
        mWarningSnackbar = Snackbar.make(mCoordinatorLayoutRoot,
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
        IntentUtil.startGmsStatusForResolution(this, status, CHECK_LOCATION_SETTINGS_REQUEST_CODE);
        mMainPresenter.setIsLocationSettingsStatusForResultCalled(true);
    }

    @Override
    public void showLocationSettingsWarning() {
        mWarningSnackbar = Snackbar.make(mCoordinatorLayoutRoot,
                R.string.warning_inadequate_location_settings,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.change_settings, v -> mMainPresenter.checkLocationSettings());
        mWarningSnackbar.show();
    }

    @Override
    public void showInadequateSettingsWarning() {
        mWarningSnackbar = Snackbar.make(mCoordinatorLayoutRoot,
                R.string.warning_inadequeate_settings,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.change_settings, v -> IntentUtil.goToAirplaneModeSettings(this));
        mWarningSnackbar.show();
    }

    @Override
    public void onGmsConnectionResultResolutionRequired(ConnectionResult connectionResult) {
        IntentUtil.startGmsConnectionResultForResolution(this, connectionResult, UNHANDLED_REQUEST_CODE);
    }

    @Override
    public void onGmsConnectionResultNoResolution(int errorCode) {
        GoogleApiAvailability.getInstance().getErrorDialog(this, errorCode, UNHANDLED_REQUEST_CODE).show();
    }

    /* Events */

    @SuppressWarnings("unused")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onPostServiceStateChangeEvent(PostServiceStateChangeEvent event) {
        LogUtil.i(TAG, "received post service state changed: " + Boolean.toString(event.isPostServiceActive()));
        mPostServiceSetProgress = event.isPostServiceActive();
        mMaterialProgressBar.setVisibility(mPresenterSetProgress || mPostServiceSetProgress ? View.VISIBLE : View.INVISIBLE);
        if (!event.isPostServiceActive()) {
            if (mPendingRefresh) {
                mMainPresenter.downloadLocationsFromServer();
                mPendingRefresh = false;
            }
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onLocationAddedToUnsentTable(SuccessfullyAddedToUnsentTableEvent event) {
        LogUtil.i(TAG, "received location record added to unsent table");
        mLocationRecordsAdapter.updateLocationRecordItem(new LocationRecordItem(event.getUnsentLocationRecord()));
        mRecyclerView.scrollToPosition(0);
        EventUtil.removeStickyEvent(event);
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onLocationSent(SuccessfullySentLocationToServerEvent event) {
        LogUtil.i(TAG, "received successfully sent location event");
        mLocationRecordsAdapter.replaceLocationRecord(event.getDeletedUnsentLocationRecord(), event.getReceivedLocationRecord());
        EventUtil.removeStickyEvent(event);
    }
}
