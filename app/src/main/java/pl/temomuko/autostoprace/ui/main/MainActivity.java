package pl.temomuko.autostoprace.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.Event;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.service.LocationSyncService;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerActivity;
import pl.temomuko.autostoprace.ui.main.adapter.LocationRecordItem;
import pl.temomuko.autostoprace.ui.main.adapter.LocationRecordsAdapter;
import pl.temomuko.autostoprace.ui.post.PostActivity;
import pl.temomuko.autostoprace.ui.staticdata.launcher.LauncherActivity;
import pl.temomuko.autostoprace.ui.teamslocationsmap.TeamsLocationsMapActivity;
import pl.temomuko.autostoprace.ui.widget.VerticalDividerItemDecoration;
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

    private static final int REQUEST_CODE_FINE_LOCATION_PERMISSION = 0;
    private static final int REQUEST_CODE_CHECK_LOCATION_SETTINGS = 1;
    private static final int UNHANDLED_REQUEST_CODE = -1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String BUNDLE_RECYCLER_VIEW_LINEAR_LAYOUT_STATE = "bundle_recycler_view_linear_layout_state";
    private static final String BUNDLE_LOCATION_RECORD_ADAPTER_ITEMS = "bundle_location_record_adapter_items";
    public static final String EXTRA_TEAM_NUMBER = "extra_team_number";
    public static final int REQUEST_CODE_POST_ACTIVITY = 2;

    @Inject MainPresenter mMainPresenter;
    @Inject LocationRecordsAdapter mLocationRecordsAdapter;
    @Inject VerticalDividerItemDecoration mVerticalDividerItemDecoration;
    @Bind(R.id.horizontal_progress_bar) MaterialProgressBar mMaterialProgressBar;
    @Bind(R.id.fab_go_to_post) FloatingActionButton mGoToPostFab;
    @Bind(R.id.recycler_view) RecyclerView mRecyclerView;
    @Bind(R.id.tv_empty_info) TextView mEmptyInfoTextView;
    @Bind(R.id.cl_root) CoordinatorLayout mCoordinatorLayoutRoot;
    @Bind(R.id.ll_empty_state) LinearLayout mEmptyStateLinearLayout;
    private Snackbar mWarningSnackbar;
    private LinearLayoutManager mRecyclerViewLinearLayoutManager;

    private CompositeSubscription mLocationRecordsUpdatesSubscriptions;

    private boolean mPostServiceSetProgress;
    private boolean mPresenterSetProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActivityComponent().inject(this);
        mMainPresenter.attachView(this);
        mLocationRecordsUpdatesSubscriptions = new CompositeSubscription();
        setupRecyclerView();
        setListeners();
        if (mMainPresenter.isAuthorized()) {
            mMainPresenter.setupUserInfoInDrawer();
            if (savedInstanceState == null) {
                mMainPresenter.loadLocations();
                startLocationSyncService();
            } else {
                restoreLocations(savedInstanceState);
            }
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
        if (mMainPresenter.isAuthorized()) {
            mMainPresenter.syncLocationsIfRecentlyNotSynced();
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
        mLocationRecordsUpdatesSubscriptions.unsubscribe();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mLocationRecordsAdapter.getItemCount() != 0 && mRecyclerViewLinearLayoutManager != null) {
            outState.putParcelableArray(BUNDLE_LOCATION_RECORD_ADAPTER_ITEMS,
                    mLocationRecordsAdapter.onSaveInstanceState());
            outState.putParcelable(BUNDLE_RECYCLER_VIEW_LINEAR_LAYOUT_STATE,
                    mRecyclerViewLinearLayoutManager.onSaveInstanceState());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CHECK_LOCATION_SETTINGS:
                resultCode = LocationSettingsUtil.getApiDependentResultCode(resultCode, data);
                mMainPresenter.setIsLocationSettingsStatusForResultCalled(false);
                mMainPresenter.handleLocationSettingsDialogResult(resultCode);
                break;
            case REQUEST_CODE_POST_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    mMainPresenter.loadLocations();
                    startLocationSyncService();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_FINE_LOCATION_PERMISSION) {
            mMainPresenter.handleFineLocationRequestPermissionResult(
                    PermissionUtil.wasAllGranted(grantResults));
        }
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
                startLocationSyncService();
                return true;
            case R.id.action_share_map:
                shareMap();
                return true;
            case R.id.action_team_map:
                openCurrentTeamMapActivity();
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
        mGoToPostFab.setOnClickListener(v -> {
            mGoToPostFab.setClickable(false);
            mMainPresenter.goToPostLocation();
        });
    }

    private void restoreLocations(Bundle savedInstanceState) {
        Parcelable recyclerViewLinearLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_VIEW_LINEAR_LAYOUT_STATE);
        Parcelable[] locationRecordAdapterItems = savedInstanceState.getParcelableArray(BUNDLE_LOCATION_RECORD_ADAPTER_ITEMS);
        if (recyclerViewLinearLayoutState != null && locationRecordAdapterItems != null) {
            mRecyclerViewLinearLayoutManager.onRestoreInstanceState(recyclerViewLinearLayoutState);
            mLocationRecordsAdapter.onRestoreInstanceState(locationRecordAdapterItems);
            showList();
        }
    }

    private void shareMap() {
        IntentUtil.shareLocationsMap(this, String.valueOf(mMainPresenter.getCurrentUserTeamNumber()));
    }

    private void openCurrentTeamMapActivity() {
        Intent intent = new Intent(this, TeamsLocationsMapActivity.class);
        intent.putExtra(EXTRA_TEAM_NUMBER, mMainPresenter.getCurrentUserTeamNumber());
        startActivity(intent);
    }

    /* MVP View methods */

    @Override
    public void updateLocationRecordsList(@NonNull List<LocationRecord> locationRecords) {
        if (mLocationRecordsAdapter.isEmpty()) {
            setLocationRecordsList(locationRecords);
        } else {
            mLocationRecordsUpdatesSubscriptions.clear();
            mLocationRecordsUpdatesSubscriptions.add(Observable.from(locationRecords)
                    .map(LocationRecordItem::new)
                    .toSortedList()
                    .compose(RxUtil.applyComputationSchedulers())
                    .subscribe(this::updateLocationRecordItemsMaintainingScrollPosition));
        }
    }

    @Override
    public void showEmptyInfo() {
        mRecyclerView.setVisibility(View.GONE);
        mEmptyStateLinearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void startLauncherActivity() {
        Intent intent = new Intent(this, LauncherActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void startPostActivity() {
        Intent intent = new Intent(this, PostActivity.class);
        startActivityForResult(intent, REQUEST_CODE_POST_ACTIVITY);
    }

    @Override
    public void startLoginActivity() {
        Intent intent = new Intent(this, LauncherActivity.class);
        startActivity(intent);
        finish();
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
    public void showSessionExpiredError() {
        Toast.makeText(this, R.string.error_session_expired, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void compatRequestFineLocationPermission() {
        PermissionUtil.requestFineLocationPermission(this, REQUEST_CODE_FINE_LOCATION_PERMISSION);
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
        IntentUtil.startGmsStatusForResolution(this, status, REQUEST_CODE_CHECK_LOCATION_SETTINGS);
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

    @Override
    public void setGoToPostLocationHandled() {
        mGoToPostFab.setClickable(true);
    }

    @Override
    public void startLocationSyncService() {
        if (!LocationSyncService.isRunning(this)) {
            startService(LocationSyncService.getStartIntent(this));
        }
    }

    /* Private helper methods */

    private void updateLocationRecordItemsMaintainingScrollPosition(List<LocationRecordItem> locationRecordItems) {
        int oldFirstVisibleItemIndex = mRecyclerViewLinearLayoutManager.findFirstVisibleItemPosition();
        int oldOffset = 0;
        View firstChild = mRecyclerViewLinearLayoutManager.getChildAt(0);
        if (firstChild != null) {
            oldOffset = firstChild.getTop();
        }
        mLocationRecordsAdapter.updateLocationRecordItems(locationRecordItems);
        mRecyclerViewLinearLayoutManager.scrollToPositionWithOffset(oldFirstVisibleItemIndex, oldOffset);
    }

    private void setLocationRecordsList(@NonNull List<LocationRecord> locationRecords) {
        if (locationRecords.isEmpty()) {
            showEmptyInfo();
        } else {
            mLocationRecordsUpdatesSubscriptions.clear();
            mLocationRecordsUpdatesSubscriptions.add(Observable.from(locationRecords)
                    .map(LocationRecordItem::new)
                    .toSortedList()
                    .compose(RxUtil.applyComputationSchedulers())
                    .subscribe(sortedLocationItems -> {
                        mLocationRecordsAdapter.setSortedLocationRecordItems(sortedLocationItems);
                        showList();
                    }));
        }
    }

    private void showList() {
        mEmptyStateLinearLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /* Events */

    @SuppressWarnings("unused")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onPostServiceStateChangeEvent(Event.PostServiceStateChanged event) {
        LogUtil.i(TAG, "received post service state changed: " + Boolean.toString(event.isPostServiceActive()));
        mPostServiceSetProgress = event.isPostServiceActive();
        mMaterialProgressBar.setVisibility(mPresenterSetProgress || mPostServiceSetProgress ? View.VISIBLE : View.INVISIBLE);
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onLocationSent(Event.SuccessfullySentLocationToServer event) {
        LogUtil.i(TAG, "received successfully sent location event");
        mLocationRecordsAdapter.replaceLocationRecord(event.getDeletedUnsentLocationRecord(), event.getReceivedLocationRecord());
        EventUtil.removeStickyEvent(event);
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onDatabaseRefreshed(Event.DatabaseRefreshed event) {
        LogUtil.i(TAG, "received database refreshed event");
        mMainPresenter.loadLocations();
        EventUtil.removeStickyEvent(event);
    }
}
