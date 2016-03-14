package pl.temomuko.autostoprace.data.event;

/**
 * Created by Rafał Naniewicz on 13.03.2016.
 */
public class PostServiceStateChangedEvent {

    private boolean mIsPostServiceActive;

    public PostServiceStateChangedEvent(boolean isPostServiceActive) {
        mIsPostServiceActive = isPostServiceActive;
    }

    public boolean isPostServiceActive() {
        return mIsPostServiceActive;
    }
}
