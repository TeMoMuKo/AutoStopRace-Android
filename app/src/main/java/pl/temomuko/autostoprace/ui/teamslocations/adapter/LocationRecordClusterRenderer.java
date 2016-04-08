package pl.temomuko.autostoprace.ui.teamslocations.adapter;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.Collection;
import java.util.Iterator;

import pl.temomuko.autostoprace.R;

/**
 * Created by Rafał Naniewicz on 02.04.2016.
 */
public class LocationRecordClusterRenderer extends DefaultClusterRenderer<LocationRecordClusterItem> {

    private Context mContext;

    public LocationRecordClusterRenderer(Context context, GoogleMap map, ClusterManager<LocationRecordClusterItem> clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
    }

    @Override
    protected void onBeforeClusterItemRendered(LocationRecordClusterItem item, MarkerOptions markerOptions) {
        markerOptions
                .title(item.getMessage())
                .snippet(item.getReceiptDateString())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.asr_marker_48dp));
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<LocationRecordClusterItem> cluster, MarkerOptions markerOptions) {
        super.onBeforeClusterRendered(cluster, markerOptions);
        setupClusterMarker(cluster, markerOptions);
    }

    private void setupClusterMarker(Cluster<LocationRecordClusterItem> cluster, MarkerOptions markerOptions) {
        LocationRecordClusterItem lastClusterItem = getLastClusterItem(cluster.getItems());
        String lastMessage = lastClusterItem.getMessage();
        if (lastMessage == null) {
            markerOptions.title(mContext.getString(R.string.msg_last_location_record_received))
                    .snippet(lastClusterItem.getReceiptDateString());
        } else {
            markerOptions.title(mContext.getString(R.string.msg_last_location_record_message) + "\n"
                    + lastClusterItem.getMessage())
                    .snippet(lastClusterItem.getReceiptDateString());
        }
    }

    private LocationRecordClusterItem getLastClusterItem(Collection<LocationRecordClusterItem> locationRecordClusterItems) {
        if (!locationRecordClusterItems.isEmpty()) {
            Iterator<LocationRecordClusterItem> locationRecordClusterItemIterator = locationRecordClusterItems.iterator();
            LocationRecordClusterItem lastLocationRecordClusterItem = locationRecordClusterItemIterator.next();
            while (locationRecordClusterItemIterator.hasNext()) {
                lastLocationRecordClusterItem = locationRecordClusterItemIterator.next();
            }
            return lastLocationRecordClusterItem;
        } else {
            return new LocationRecordClusterItem(0, 0, "something went wrong", null);
        }
    }
}
