package pl.temomuko.autostoprace.ui.teamslocationsmap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.model.Team;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerActivity;
import pl.temomuko.autostoprace.ui.main.MainActivity;
import pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.map.LocationRecordClusterItem;
import pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.map.LocationRecordClusterRenderer;
import pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.map.TeamLocationInfoWindowAdapter;
import pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.searchteamview.SearchTeamView;
import pl.temomuko.autostoprace.util.IntentUtil;
import pl.temomuko.autostoprace.util.rx.RxCacheHelper;
import pl.temomuko.autostoprace.util.rx.RxUtil;
import rx.Observable;
import rx.Subscription;

/**
 * Created by Szymon Kozak on 2016-02-05.
 */
public class TeamsLocationsMapActivity extends DrawerActivity
        implements TeamsLocationsMapMvpView {

    private static final String TAG = TeamsLocationsMapActivity.class.getSimpleName();
    private static final String BUNDLE_CURRENT_TEAM_LOCATIONS = "bundle_current_team_locations";
    private static final String BUNDLE_SEARCH_TEAM_VIEW = "bundle_team_list_hints";
    private static final float DEFAULT_MAP_ZOOM = 5.5f;
    private final static String RX_CACHE_ALL_TEAMS_TAG = "rx_cache_all_teams_tag";
    public static final String RX_CACHE_TEAM_LOCATIONS_TAG = "rx_cache_team_locations_tag";

    @Inject TeamsLocationsMapPresenter mTeamsLocationsMapPresenter;
    @Inject TeamLocationInfoWindowAdapter mTeamsLocationInfoWindowAdapter;

    @Bind(R.id.horizontal_progress_bar) MaterialProgressBar mMaterialProgressBar;
    @Bind(R.id.search_team_view) SearchTeamView mSearchTeamView;
    @Bind(R.id.rv_team_hints) RecyclerView mTeamHintsRecyclerView;
    @Bind(R.id.card_team_hints) CardView mTeamHintsLinearLayout;

    private boolean mAllTeamsProgressState = false;
    private boolean mTeamProgressState = false;
    private boolean mAnimateTeamLocationsUpdate = true;
    private GoogleMap mMap;
    private Subscription mSetHintsSubscription;
    private Subscription mSetLocationsSubscription;
    private ClusterManager<LocationRecordClusterItem> mClusterManager;
    private List<LocationRecord> mCurrentTeamLocations;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams_location);
        getActivityComponent().inject(this);
        setupPresenter();
        setupMapFragment();
        setupIntent(getIntent());
        setupSearchTeamView();
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

    private void setupIntent(Intent intent) {
        Uri data = intent.getData();
        Bundle extras = intent.getExtras();
        if (data != null) {
            String teamNumberParameterValue = data.getQueryParameter(Constants.URL_MAP_TEAM_NUMBER_PARAM);
            if (teamNumberParameterValue != null) {
                changeTeamFromIntent(teamNumberParameterValue);
            }
        } else if (extras != null) {
            changeTeam(extras.getInt(MainActivity.EXTRA_TEAM_NUMBER));
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
        mSearchTeamView.requestSearch();
    }

    private void setupMapFragment() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            mTeamsLocationsMapPresenter.loadAllTeams();
            mSearchTeamView.setEnabled(true);
            setupClusterManager();
            if (mCurrentTeamLocations != null) {
                mAnimateTeamLocationsUpdate = false;
                setLocations(mCurrentTeamLocations);
            }
        });
    }

    private void setupClusterManager() {
        mClusterManager = new ClusterManager<>(getApplicationContext(), mMap);
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
        mClusterManager.setRenderer(new LocationRecordClusterRenderer(getApplicationContext(), mMap, mClusterManager));
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
    public void setLocations(@NonNull List<LocationRecord> locationRecords) {
        mCurrentTeamLocations = locationRecords;
        if (mSetLocationsSubscription != null) mSetLocationsSubscription.unsubscribe();
        mSetLocationsSubscription = Observable.from(locationRecords)
                .map(LocationRecordClusterItem::new)
                .toSortedList()
                .compose(RxUtil.applyComputationSchedulers())
                .subscribe(this::handleTeamLocationsToSet);
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
    public void showNoLocationRecordsInfo() {
        Toast.makeText(this, R.string.msg_no_location_records_to_display, Toast.LENGTH_SHORT).show();
    }

    /* Private helper methods */

    private void handleTeamLocationsToSet(List<LocationRecordClusterItem> locationRecordClusterItems) {
        if (mAnimateTeamLocationsUpdate) {
            if (!locationRecordClusterItems.isEmpty()) {
                mMap.animateCamera(CameraUpdateFactory
                        .newLatLngZoom(locationRecordClusterItems.get(0).getPosition(), DEFAULT_MAP_ZOOM));
            }
        } else {
            mAnimateTeamLocationsUpdate = true;
        }
        mClusterManager.clearItems();
        mClusterManager.addItems(locationRecordClusterItems);
        mClusterManager.cluster();
    }
}
