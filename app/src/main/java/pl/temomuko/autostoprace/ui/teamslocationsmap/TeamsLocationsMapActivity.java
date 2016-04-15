package pl.temomuko.autostoprace.ui.teamslocationsmap;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.maps.android.clustering.ClusterManager;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

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
import pl.temomuko.autostoprace.util.rx.RxUtil;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Szymon Kozak on 2016-02-05.
 */
public class TeamsLocationsMapActivity extends DrawerActivity
        implements TeamsLocationsMapMvpView, OnMapReadyCallback, SearchTeamView.OnTeamRequestedListener {

    private static final String TAG = TeamsLocationsMapActivity.class.getSimpleName();

    @Inject TeamsLocationsMapPresenter mTeamsLocationsMapPresenter;
    @Inject TeamLocationInfoWindowAdapter mTeamsLocationInfoWindowAdapter;

    @Bind(R.id.horizontal_progress_bar) MaterialProgressBar mMaterialProgressBar;
    @Bind(R.id.search_team_view) SearchTeamView mSearchTeamView;

    private boolean mAllTeamsProgressState = false;
    private boolean mTeamProgressState = false;
    private GoogleMap mMap;
    private CompositeSubscription mSubscriptions;
    private ConcurrentLinkedQueue<LocationRecordClusterItem> mMapNotReadyQueue;
    private ClusterManager<LocationRecordClusterItem> mClusterManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams_location);
        getActivityComponent().inject(this);
        mTeamsLocationsMapPresenter.attachView(this);
        mTeamsLocationsMapPresenter.setupUserInfoInDrawer();
        mMapNotReadyQueue = new ConcurrentLinkedQueue<>();
        mSubscriptions = new CompositeSubscription();
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
        setupTeamSearchView();
    }

    private void setupTeamSearchView() {
        mSearchTeamView.setOnTeamRequestedListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        //// TODO: 15.04.2016
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
                mTeamsLocationsMapPresenter.loadTeam(mSearchTeamView.getText().toString());
                mSearchTeamView.closeSearch();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mTeamsLocationsMapPresenter.detachView();
        mSubscriptions.unsubscribe();
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mTeamsLocationsMapPresenter.loadAllTeams();
        mSearchTeamView.setEnabled(true);
        setupClusterManager();
        addMarkersFromQueue();
    }

    private void setupClusterManager() {
        mClusterManager = new ClusterManager<>(getApplicationContext(), mMap);
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
        mClusterManager.setRenderer(new LocationRecordClusterRenderer(getApplicationContext(), mMap, mClusterManager));
        mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(mTeamsLocationInfoWindowAdapter);
        mClusterManager.getClusterMarkerCollection().setOnInfoWindowAdapter(mTeamsLocationInfoWindowAdapter);
    }

    private void addMarkersFromQueue() {
        if (!mMapNotReadyQueue.isEmpty()) {
            for (LocationRecordClusterItem locationRecordClusterItem : mMapNotReadyQueue) {
                mClusterManager.addItem(locationRecordClusterItem);
            }
            mMapNotReadyQueue.clear();
        }
    }

    /* MVP View methods */

    @Override
    public void setLocations(@NonNull List<LocationRecord> locationRecords) {
        mSubscriptions.clear();
        mSubscriptions.add(Observable.from(locationRecords)
                .map(LocationRecordClusterItem::new)
                .toList()
                .compose(RxUtil.applyComputationSchedulers())
                .subscribe(this::handleLocationsToSet)
        );
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setHints(List<Team> teams) {
        mSubscriptions.add(
                Observable.just(teams)
                        .map(unsortedTeams -> {
                            Collections.sort(unsortedTeams);
                            return unsortedTeams;
                        })
                        .compose(RxUtil.applyComputationSchedulers())
                        .subscribe(mSearchTeamView::setHints)

        );
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

    private void handleLocationsToSet(List<LocationRecordClusterItem> locationRecordClusterItems) {
        if (mMap == null || mClusterManager == null) {
            mMapNotReadyQueue.clear();
            mMapNotReadyQueue.addAll(locationRecordClusterItems);
        } else {
            mClusterManager.clearItems();
            mClusterManager.addItems(locationRecordClusterItems);
            mClusterManager.cluster();
        }
    }

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
