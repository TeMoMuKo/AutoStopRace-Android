package pl.temomuko.autostoprace.ui.teamslocationsmap;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Iterator;

import pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.map.LocationRecordClusterItem;

public final class ClasterUtil {

    private ClasterUtil() {
        throw new AssertionError();
    }

    public static LocationRecordClusterItem getNewestClusterItem(@NonNull Collection<LocationRecordClusterItem> locationRecordClusterItems) {
        Iterator<LocationRecordClusterItem> itemsIterator = locationRecordClusterItems.iterator();
        LocationRecordClusterItem currentLocationRecordCluster;
        LocationRecordClusterItem newestLocationRecordClusterItem = itemsIterator.next();
        while (itemsIterator.hasNext()) {
            currentLocationRecordCluster = itemsIterator.next();
            if (newestLocationRecordClusterItem.getReceiptDate().before(
                    currentLocationRecordCluster.getReceiptDate())) {
                newestLocationRecordClusterItem = currentLocationRecordCluster;
            }
        }
        return newestLocationRecordClusterItem;
    }
}
