package pl.temomuko.autostoprace.ui.teamslocationsmap;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.maps.android.clustering.ClusterManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.data.Event;
import pl.temomuko.autostoprace.data.local.LocationsViewMode;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.model.Team;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerActivity;
import pl.temomuko.autostoprace.ui.main.MainActivity;
import pl.temomuko.autostoprace.ui.main.Shortcuts;
import pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.map.LocationRecordClusterItem;
import pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.map.LocationRecordClusterRenderer;
import pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.map.TeamLocationInfoWindowAdapter;
import pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.searchteamview.SearchTeamView;
import pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.wall.FirstItemTopMarginDecoration;
import pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.wall.WallAdapter;
import pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.wall.WallItem;
import pl.temomuko.autostoprace.ui.widget.FullScreenImageDialog;
import pl.temomuko.autostoprace.util.IntentUtil;
import pl.temomuko.autostoprace.util.LogUtil;
import pl.temomuko.autostoprace.util.rx.RxCacheHelper;
import pl.temomuko.autostoprace.util.rx.RxUtil;
import rx.Observable;
import rx.Subscription;
import pl.temomuko.autostoprace.R;

public class TeamsLocationsMapActivity extends DrawerActivity
        implements TeamsLocationsMapMvpView {

    private static final String TAG = TeamsLocationsMapActivity.class.getSimpleName();
    private static final String BUNDLE_CURRENT_TEAM_LOCATIONS = "bundle_current_team_locations";
    private static final String BUNDLE_SEARCH_TEAM_VIEW = "bundle_team_list_hints";
    private static final float DEFAULT_MAP_ZOOM = 5.5f;
    private static final String RX_CACHE_ALL_TEAMS_TAG = "rx_cache_all_teams_tag";
    public static final String RX_CACHE_TEAM_LOCATIONS_TAG = "rx_cache_team_locations_tag";
    public static final String BUNDLE_IS_CONSUMED_TOOLBAR_INTENT = "bundle_is_consumed_intent";
    public static final String BUNDLE_IS_CONSUMED_URI_INTENT = "bundle_is_consumed_uri_intent";

    @Inject TeamsLocationsMapPresenter mTeamsLocationsMapPresenter;
    @Inject TeamLocationInfoWindowAdapter mTeamsLocationInfoWindowAdapter;
    @Inject WallAdapter mWallAdapter;

    @BindView(R.id.horizontal_progress_bar) MaterialProgressBar mMaterialProgressBar;
    @BindView(R.id.search_team_view) SearchTeamView mSearchTeamView;
    @BindView(R.id.rv_team_hints) RecyclerView mTeamHintsRecyclerView;
    @BindView(R.id.card_team_hints) CardView mTeamHintsLinearLayout;
    @BindView(R.id.rv_wall) RecyclerView mWallRecyclerView;
    @BindView(R.id.bottom_bar) BottomNavigationView mBottomNavigationView;

    private boolean mAllTeamsProgressState = false;
    private boolean mTeamProgressState = false;
    private boolean mAnimateTeamLocationsUpdate = true;
    private boolean mIsConsumedToolbarIntent = false;
    private boolean mIsConsumedUriIntent = false;
    private GoogleMap mMap;
    private Subscription mSetHintsSubscription;
    private Subscription mSetLocationsSubscription;
    private ClusterManager<LocationRecordClusterItem> mClusterManager;
    private List<LocationRecord> mCurrentTeamLocations;

    public static void start(Context context) {
        Intent starter = new Intent(context, TeamsLocationsMapActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams_locations_map);
        getActivityComponent().inject(this);
        setupBottomNavigationView();
        setupPresenter();
        setupSearchTeamView();
        setupIntentInstanceState(savedInstanceState);
        setupWall();
        setupMapFragment();
        reportShortcutUsage(Shortcuts.LOCATIONS_MAP);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTeamsLocationsMapPresenter.loadAllTeams();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        mTeamsLocationsMapPresenter.detachView();
        if (mSetHintsSubscription != null) mSetHintsSubscription.unsubscribe();
        if (mSetLocationsSubscription != null) mSetLocationsSubscription.unsubscribe();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mCurrentTeamLocations != null) {
            outState.putParcelableArray(BUNDLE_CURRENT_TEAM_LOCATIONS,
                    mCurrentTeamLocations.toArray(new LocationRecord[mCurrentTeamLocations.size()]));
        }
        outState.putBundle(BUNDLE_SEARCH_TEAM_VIEW, mSearchTeamView.saveHintsState());
        outState.putBoolean(BUNDLE_IS_CONSUMED_TOOLBAR_INTENT, mIsConsumedToolbarIntent);
        outState.putBoolean(BUNDLE_IS_CONSUMED_URI_INTENT, mIsConsumedUriIntent);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSearchTeamView.restoreHintState(savedInstanceState.getBundle(BUNDLE_SEARCH_TEAM_VIEW));
        Parcelable[] parcelableCurrentTeamLocations =
                savedInstanceState.getParcelableArray(BUNDLE_CURRENT_TEAM_LOCATIONS);
        if (parcelableCurrentTeamLocations != null) {
            mCurrentTeamLocations = new ArrayList<>(parcelableCurrentTeamLocations.length);
            for (Parcelable parcelable : parcelableCurrentTeamLocations) {
                mCurrentTeamLocations.add((LocationRecord) parcelable);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_teams_locations_map, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                handleActionSearch();
                return true;
            case R.id.action_share_map:
                shareMap();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mSearchTeamView.hasFocus()) {
            mSearchTeamView.clearFocus();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setupIntent(intent);
    }

    private void setupPresenter() {
        mTeamsLocationsMapPresenter.setupRxCacheHelper(this,
                RxCacheHelper.get(RX_CACHE_ALL_TEAMS_TAG),
                RxCacheHelper.get(RX_CACHE_TEAM_LOCATIONS_TAG));
        mTeamsLocationsMapPresenter.attachView(this);
        mTeamsLocationsMapPresenter.setupUserInfoInDrawer();
    }

    private void setupSearchTeamView() {
        mSearchTeamView.setHintsRecyclerView(mTeamHintsRecyclerView);
        mSearchTeamView.setOptionalHintsView(mTeamHintsLinearLayout);
        mSearchTeamView.setOnTeamRequestedListener(new SearchTeamView.OnTeamRequestedListener() {
            @Override
            public void onTeamRequest(int teamId) {
                mTeamsLocationsMapPresenter.loadTeam(teamId);
            }

            @Override
            public void onTeamRequest(String teamString) {
                mTeamsLocationsMapPresenter.loadTeam(teamString);
            }
        });
    }

    private void setupIntentInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mIsConsumedToolbarIntent = savedInstanceState.getBoolean(BUNDLE_IS_CONSUMED_TOOLBAR_INTENT);
            mIsConsumedUriIntent = savedInstanceState.getBoolean(BUNDLE_IS_CONSUMED_URI_INTENT);
        }
        setupIntent(getIntent());
    }

    private void setupIntent(Intent intent) {
        Uri data = intent.getData();
        Bundle extras = intent.getExtras();
        if (data != null && !mIsConsumedUriIntent) {
            String teamNumberParameterValue = data.getQueryParameter(Constants.URL_MAP_TEAM_NUMBER_PARAM);
            if (teamNumberParameterValue != null) {
                changeTeamFromIntent(teamNumberParameterValue);
                mIsConsumedUriIntent = true;
            }
        } else if (extras != null && !mIsConsumedToolbarIntent) {
            changeTeam(extras.getInt(MainActivity.EXTRA_TEAM_NUMBER));
            mIsConsumedToolbarIntent = true;
        }
    }

    private void changeTeamFromIntent(@NonNull String teamNumberParameterValue) {
        try {
            int teamNumber = Integer.parseInt(teamNumberParameterValue.replaceAll("[\\D]", ""));
            changeTeam(teamNumber);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid url query param.");
        }
    }

    private void changeTeam(int teamNumber) {
        mSearchTeamView.setText(String.valueOf(teamNumber));
        mTeamsLocationsMapPresenter.loadTeam(teamNumber);
    }

    private void setupWall() {
        mWallRecyclerView.setHasFixedSize(true);
        float firstItemMargin = getResources().getDimension(R.dimen.margin_wall_item);
        FirstItemTopMarginDecoration decoration = new FirstItemTopMarginDecoration(firstItemMargin);
        mWallRecyclerView.addItemDecoration(decoration);
        mWallRecyclerView.setAdapter(mWallAdapter);
    }

    private void setupMapFragment() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(googleMap -> {
            googleMap.clear();
            mMap = googleMap;
            mSearchTeamView.setEnabled(true);
            setupClusterManager();
            if (mCurrentTeamLocations != null) {
                mAnimateTeamLocationsUpdate = false;
                setLocationsForMap(mCurrentTeamLocations);
            }
        });
    }

    private void setupBottomNavigationView() {
        mBottomNavigationView.setOnNavigationItemSelectedListener((menuItem) -> {
            switch (menuItem.getItemId()) {
                case R.id.map:
                    mTeamsLocationsMapPresenter.updateLocationsViewModeContent(LocationsViewMode.MAP);
                    return true;
                case R.id.wall:
                    mTeamsLocationsMapPresenter.updateLocationsViewModeContent(LocationsViewMode.WALL);
                    return true;
            }
            return false;
        });
    }

    private void setupClusterManager() {
        mClusterManager = new ClusterManager<>(getApplicationContext(), mMap);

        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);

        mClusterManager.setRenderer(new LocationRecordClusterRenderer(getApplicationContext(), mMap, mClusterManager));
        mClusterManager.setOnClusterItemInfoWindowClickListener(locationRecordClusterItem ->
                mTeamsLocationsMapPresenter.handleMarkerClick(locationRecordClusterItem.getImageUri()));

        mClusterManager.setOnClusterInfoWindowClickListener(cluster ->
                mTeamsLocationsMapPresenter.handleClusterMarkerClick(cluster.getItems()));
        setCustomClusterWindowAdapter();
    }

    private void setCustomClusterWindowAdapter() {
        mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(mTeamsLocationInfoWindowAdapter);
        mClusterManager.getClusterMarkerCollection().setOnInfoWindowAdapter(mTeamsLocationInfoWindowAdapter);
    }

    private void handleActionSearch() {
        if (mSearchTeamView.hasFocus()) {
            mSearchTeamView.requestSearch();
        } else {
            mSearchTeamView.openSearch();
        }
    }

    private void shareMap() {
        IntentUtil.shareLocationsMap(this, mSearchTeamView.getText().toString());
    }

    /* MVP View methods */

    @Override
    public void setAllTeamsProgress(boolean allTeamsProgressState) {
        mAllTeamsProgressState = allTeamsProgressState;
        mMaterialProgressBar.setVisibility(
                allTeamsProgressState || mTeamProgressState ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void setTeamProgress(boolean teamProgressState) {
        mTeamProgressState = teamProgressState;
        mMaterialProgressBar.setVisibility(
                teamProgressState || mAllTeamsProgressState ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void setHints(List<Team> teams) {
        if (mSetHintsSubscription != null) mSetHintsSubscription.unsubscribe();
        mSetHintsSubscription = Observable.from(teams)
                .toSortedList()
                .compose(RxUtil.applyComputationSchedulers())
                .subscribe(mSearchTeamView::setHints);
    }

    @Override
    public void clearCurrentTeamLocations() {
        if (mClusterManager != null) {
            mClusterManager.clearItems();
            mClusterManager.cluster();
        }
        if (mCurrentTeamLocations != null) {
            mCurrentTeamLocations.clear();
        }
    }

    @Override
    public void setLocationsForMap(@NonNull List<LocationRecord> locationRecords) {
        mCurrentTeamLocations = locationRecords;
        if (mSetLocationsSubscription != null) mSetLocationsSubscription.unsubscribe();
        mSetLocationsSubscription = Observable.from(locationRecords)
                .map(LocationRecordClusterItem::new)
                .toSortedList()
                .compose(RxUtil.applyComputationSchedulers())
                .subscribe(this::handleTeamLocationsToSet);
    }

    @Override
    public void setWallItems(List<WallItem> wallItems) {
        mWallAdapter.setWallItems(wallItems);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showInvalidFormatError() {
        Toast.makeText(this, getString(R.string.error_invalid_team_id), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNoLocationRecordsInfoForMap() {
        Toast.makeText(this, R.string.msg_no_location_records_to_display, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNoLocationRecordsInfoForWall() {
        //todo
    }

    @Override
    public void openFullscreenImage(Uri imageUri) {
        FullScreenImageDialog.newInstance(imageUri).show(getSupportFragmentManager(), FullScreenImageDialog.TAG);
    }

    @Override
    public void setLocationsViewMode(LocationsViewMode mode) {
        switch (mode) {
            case MAP:
                mBottomNavigationView.setSelectedItemId(R.id.map);
                break;
            case WALL:
                mBottomNavigationView.setSelectedItemId(R.id.wall);
                break;
        }
    }

    @Override
    public void setWallVisible(boolean visible) {
        mWallRecyclerView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /* Private helper methods */

    private void handleTeamLocationsToSet(List<LocationRecordClusterItem> locationRecordClusterItems) {
        if (mClusterManager != null) {
            mClusterManager.addItems(locationRecordClusterItems);
            mClusterManager.cluster();
            if (mAnimateTeamLocationsUpdate) {
                if (!locationRecordClusterItems.isEmpty()) {
                    mMap.animateCamera(CameraUpdateFactory
                            .newLatLngZoom(locationRecordClusterItems.get(0).getPosition(), DEFAULT_MAP_ZOOM));
                }
            } else {
                mAnimateTeamLocationsUpdate = true;
            }
        }
    }

    /* Events */

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkConnected(Event.NetworkConnected event) {
        LogUtil.i(TAG, "received network connected event");
        mTeamsLocationsMapPresenter.loadAllTeams();
    }
}
