package pl.temomuko.autostoprace.ui.teamslocationsmap;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.model.Team;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerActivity;
import pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.LocationRecordClusterItem;
import pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.LocationRecordClusterRenderer;
import pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.TeamLocationInfoWindowAdapter;
import pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.searchteamview.SearchTeamView;
import pl.temomuko.autostoprace.util.rx.RxCacheHelper;
import pl.temomuko.autostoprace.util.rx.RxUtil;
import rx.Observable;
import rx.Subscription;

/**
 * Created by Szymon Kozak on 2016-02-05.
 */
public class TeamsLocationsMapActivity extends DrawerActivity
        implements TeamsLocationsMapMvpView, OnMapReadyCallback, SearchTeamView.OnTeamRequestedListener {

    private static final String TAG = TeamsLocationsMapActivity.class.getSimpleName();
    private static final String BUNDLE_CURRENT_TEAM_LOCATIONS = "bundle_current_team_locations";
    private static final String BUNDLE_TEAM_LIST_HINTS = "bundle_team_list_hints";
    private static final float DEFAULT_MAP_ZOOM = 5.5f;
    private final static String RX_CACHE_ALL_TEAMS_TAG = "rx_cache_all_teams_tag";
    public static final String RX_CACHE_TEAM_LOCATIONS_TAG = "rx_cache_team_locations_tag";

    @Inject TeamsLocationsMapPresenter mTeamsLocationsMapPresenter;
    @Inject TeamLocationInfoWindowAdapter mTeamsLocationInfoWindowAdapter;

    @Bind(R.id.horizontal_progress_bar) MaterialProgressBar mMaterialProgressBar;
    @Bind(R.id.search_team_view) SearchTeamView mSearchTeamView;

    private boolean mAllTeamsProgressState = false;
    private boolean mTeamProgressState = false;
    private GoogleMap mMap;
    private Subscription mSetHintsSubscription;
    private Subscription mSetLocationsSubscription;
    private ClusterManager<LocationRecordClusterItem> mClusterManager;
    private List<LocationRecord> mCurrentTeamLocations;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams_location);
        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }
        getActivityComponent().inject(this);
        setupPresenter();
        mSearchTeamView.setOnTeamRequestedListener(this);
        setupMapFragment();
    }

    private void restoreInstanceState(@NonNull Bundle savedInstanceState) {
        restoreLocationRecordClusterItems(savedInstanceState);
        mSearchTeamView.restoreHintState(savedInstanceState.getParcelableArray(BUNDLE_TEAM_LIST_HINTS));
    }

    private void restoreLocationRecordClusterItems(@NonNull Bundle savedInstanceState) {
        Parcelable[] parcelableCurrentTeamLocations =
                savedInstanceState.getParcelableArray(BUNDLE_CURRENT_TEAM_LOCATIONS);
        if (parcelableCurrentTeamLocations != null) {
            mCurrentTeamLocations = new ArrayList<>(parcelableCurrentTeamLocations.length);
            for (Parcelable parcelable : parcelableCurrentTeamLocations) {
                mCurrentTeamLocations.add((LocationRecord) parcelable);
            }
        }
    }

    private void setupPresenter() {
        mTeamsLocationsMapPresenter.setupRxCacheHelper(this,
                RxCacheHelper.get(RX_CACHE_ALL_TEAMS_TAG),
                RxCacheHelper.get(RX_CACHE_TEAM_LOCATIONS_TAG));
        mTeamsLocationsMapPresenter.attachView(this);
        mTeamsLocationsMapPresenter.setupUserInfoInDrawer();
    }

    private void setupMapFragment() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mTeamsLocationsMapPresenter.loadAllTeams();
        mSearchTeamView.setEnabled(true);
        setupClusterManager();
        if (mCurrentTeamLocations != null) {
            setLocations(mCurrentTeamLocations);
        }
    }

    private void setupClusterManager() {
        mClusterManager = new ClusterManager<>(getApplicationContext(), mMap);
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
        mClusterManager.setRenderer(new LocationRecordClusterRenderer(getApplicationContext(), mMap, mClusterManager));

        mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(mTeamsLocationInfoWindowAdapter);
        mClusterManager.getClusterMarkerCollection().setOnInfoWindowAdapter(mTeamsLocationInfoWindowAdapter);
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
                if (mSearchTeamView.hasFocus()) {
                    mTeamsLocationsMapPresenter.loadTeam(mSearchTeamView.getText().toString());
                    mSearchTeamView.closeSearch();
                } else {
                    mSearchTeamView.openSearch();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mCurrentTeamLocations != null) {
            outState.putParcelableArray(BUNDLE_CURRENT_TEAM_LOCATIONS,
                    mCurrentTeamLocations
                            .toArray(new LocationRecord[mCurrentTeamLocations.size()]));
        }
        outState.putParcelableArray(BUNDLE_TEAM_LIST_HINTS, mSearchTeamView.saveHintState());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        mTeamsLocationsMapPresenter.detachView();
        if (mSetHintsSubscription != null) mSetHintsSubscription.unsubscribe();
        if (mSetLocationsSubscription != null) mSetLocationsSubscription.unsubscribe();
        super.onDestroy();
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
    public void showTeamNotFoundError() {
        Toast.makeText(this, R.string.msg_team_not_found, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNoLocationRecordsInfo() {
        Toast.makeText(this, R.string.msg_no_location_records_to_display, Toast.LENGTH_SHORT).show();
    }

    private void handleTeamLocationsToSet(List<LocationRecordClusterItem> locationRecordClusterItems) {
        if (!locationRecordClusterItems.isEmpty()) {
            mMap.animateCamera(CameraUpdateFactory
                    .newLatLngZoom(locationRecordClusterItems.get(0).getPosition(), DEFAULT_MAP_ZOOM));
        }
        mClusterManager.clearItems();
        mClusterManager.addItems(locationRecordClusterItems);
        mClusterManager.cluster();
    }

    /* SearchTeamView callback methods */

    @Override
    public void onTeamRequest(int teamId) {
        mTeamsLocationsMapPresenter.loadTeam(teamId);
    }

    @Override
    public void onTeamRequest(String teamString) {
        mTeamsLocationsMapPresenter.loadTeam(teamString);
    }
}
