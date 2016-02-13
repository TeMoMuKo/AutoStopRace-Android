package pl.temomuko.autostoprace.util;

import de.greenrobot.event.EventBus;

/**
 * Created by szymen on 2016-01-09.
 */

public class EventPoster {

    public static void post(Object event) {
        EventBus.getDefault().post(event);
    }

    public static void postSticky(Object event) {
        EventBus.getDefault().postSticky(event);
    }
}
