package pl.temomuko.autostoprace.data.event;

import pl.temomuko.autostoprace.data.model.Location;

/**
 * Created by szymen on 2016-02-13.
 */
public class RemovedLocationEvent {

    Location mLocation;

    public RemovedLocationEvent(Location location) {
        mLocation = location;
    }

    public Location getLocation() {
        return mLocation;
    }
}
