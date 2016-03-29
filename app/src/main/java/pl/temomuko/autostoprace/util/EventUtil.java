package pl.temomuko.autostoprace.util;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Szymon Kozak on 2016-01-09.
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

    public static void removeStickyEvent(Object event) {
        EventBus.getDefault().removeStickyEvent(event);
    }

    public static void clearStickyEvents(Class eventClass) {
        Object event = EventBus.getDefault().getStickyEvent(eventClass);
        if (event != null) {
            EventBus.getDefault().removeStickyEvent((eventClass.cast(event)));
        }
    }
}
