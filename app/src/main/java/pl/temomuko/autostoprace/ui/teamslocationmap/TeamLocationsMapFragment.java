package pl.temomuko.autostoprace.ui.teamslocationmap;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.ui.base.BaseActivity;
import pl.temomuko.autostoprace.ui.teamslocationmap.adapter.LocationRecordClusterItem;
import pl.temomuko.autostoprace.ui.teamslocationmap.adapter.LocationRecordClusterRenderer;
import pl.temomuko.autostoprace.ui.teamslocationmap.adapter.TeamLocationInfoWindowAdapter;
import pl.temomuko.autostoprace.util.rx.RxUtil;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rafał Naniewicz on 02.04.2016.
 */
public class TeamLocationsMapFragment extends Fragment implements TeamLocationsMapMvpView, OnMapReadyCallback {

    @Bind(R.id.map_view) MapView mMapView;
    @Inject TeamLocationInfoWindowAdapter mTeamsLocationInfoWindowAdapter;
    @Inject TeamLocationsMapPresenter mTeamLocationsMapPresenter;
    private ClusterManager<LocationRecordClusterItem> mClusterManager;
    private GoogleMap mMap;
    private ConcurrentLinkedQueue<LocationRecordClusterItem> mMapNotReadyQueue;
    private CompositeSubscription mSubscriptions;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).getActivityComponent().inject(this);
        mTeamLocationsMapPresenter.attachView(this);
        mSubscriptions = new CompositeSubscription();
        mMapNotReadyQueue = new ConcurrentLinkedQueue<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentTeamsLocationView = inflater.inflate(R.layout.fragment_teams_locations_map, container, false);
        ButterKnife.bind(this, fragmentTeamsLocationView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        return fragmentTeamsLocationView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mTeamLocationsMapPresenter.detachView();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setupClusterManager();
        addMarkersFromQueue();
    }

    private void setupClusterManager() {
        mClusterManager = new ClusterManager<>(getContext(), mMap);
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
        mClusterManager.setRenderer(new LocationRecordClusterRenderer(getContext(), mMap, mClusterManager));
        mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(mTeamsLocationInfoWindowAdapter);
        mClusterManager.getClusterMarkerCollection().setOnInfoWindowAdapter(mTeamsLocationInfoWindowAdapter);
    }

    private void addMarkersFromQueue() {
        if (!mMapNotReadyQueue.isEmpty()) {
            for (LocationRecordClusterItem locationRecordClusterItem : mMapNotReadyQueue) {
                mClusterManager.addItem(locationRecordClusterItem);
            }
        }
    }

    public void addLocation(LocationRecord locationRecord) {
        LocationRecordClusterItem locationRecordClusterItem = new LocationRecordClusterItem(
                locationRecord.getLatitude(), locationRecord.getLongitude(),
                locationRecord.getMessage(), locationRecord.getServerReceiptDate());
        if (mMap == null) {
            mMapNotReadyQueue.offer(locationRecordClusterItem);
        } else {
            mClusterManager.addItem(locationRecordClusterItem);
        }
    }

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
    public void showError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    /*Load Given Teams methods*/

    public void display(int teamId) {
        mTeamLocationsMapPresenter.loadTeam(teamId);
    }
}
