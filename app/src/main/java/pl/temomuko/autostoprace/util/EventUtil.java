package pl.temomuko.autostoprace.util;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by szymen on 2016-01-09.
 */

public final class EventUtil {

    private EventUtil() {
        throw new AssertionError();
    }

    public static void post(Object event) {
        EventBus.getDefault().post(event);
    }

    public static void postSticky(Object event) {
        EventBus.getDefault().postSticky(event);
    }
}
