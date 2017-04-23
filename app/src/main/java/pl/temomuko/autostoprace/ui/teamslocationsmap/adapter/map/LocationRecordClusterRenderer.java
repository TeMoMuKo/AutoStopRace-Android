package pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.map;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.teamslocationsmap.ClasterUtil;

public class LocationRecordClusterRenderer extends DefaultClusterRenderer<LocationRecordClusterItem> {

    private static final int MIN_CLUSTER_SIZE = 10;

    private final Context mContext;
    private final String emptyTitle;
    private final String pressToSeeThePhoto;

    public LocationRecordClusterRenderer(Context context, GoogleMap map, ClusterManager<LocationRecordClusterItem> clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
        emptyTitle = mContext.getString(R.string.msg_location_record_received);
        pressToSeeThePhoto = mContext.getString(R.string.msg_press_to_see_the_photo);
    }

    @Override
    protected void onBeforeClusterItemRendered(LocationRecordClusterItem item, MarkerOptions markerOptions) {
        Uri imageUri = item.getImageUri();

        final String itemTitle = item.getTitle();
        final String title = TextUtils.isEmpty(itemTitle) ? emptyTitle : itemTitle;

        String snippet;
        final String itemSnippet = item.getSnippet();

        if (imageUri == null) {
            snippet = itemSnippet;
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.asr_marker_48dp));
        } else {
            snippet = getMarkerWithPhotoSnippet(itemSnippet);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.asr_marker_photo_48dp));
        }

        markerOptions
                .title(title)
                .snippet(snippet);
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<LocationRecordClusterItem> cluster, MarkerOptions markerOptions) {
        super.onBeforeClusterRendered(cluster, markerOptions);
        if (!cluster.getItems().isEmpty()) {
            LocationRecordClusterItem lastClusterItem = ClasterUtil.getNewestClusterItem(cluster.getItems());
            final String lastTitle = lastClusterItem.getTitle();
            final String lastSnippet = lastClusterItem.getSnippet();
            final Uri lastImageUri = lastClusterItem.getImageUri();

            if (lastTitle == null) {
                markerOptions.title(mContext.getString(R.string.msg_last_location_record_received));
            } else {
                markerOptions.title(mContext.getString(R.string.msg_last_location_record_message) + "\n"
                        + lastTitle);
            }

            if (lastImageUri == null) {
                markerOptions.snippet(lastSnippet);
            } else {
                markerOptions.snippet(getMarkerWithPhotoSnippet(lastSnippet));
            }

        }
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<LocationRecordClusterItem> cluster) {
        return cluster.getSize() > MIN_CLUSTER_SIZE;
    }

    @NonNull
    private String getMarkerWithPhotoSnippet(String lastSnippet) {
        return lastSnippet + "\n" + pressToSeeThePhoto;
    }
}
