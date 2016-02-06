package pl.temomuko.autostoprace.util;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.greenrobot.event.EventBus;

/**
 * Created by szymen on 2016-01-09.
 */

@Singleton
public class EventHelper {

    @Inject
    public EventHelper() {
    }

    public void post(Object event) {
        EventBus.getDefault().post(event);
    }

    public void postSticky(Object event) {
        EventBus.getDefault().postSticky(event);
    }
}
