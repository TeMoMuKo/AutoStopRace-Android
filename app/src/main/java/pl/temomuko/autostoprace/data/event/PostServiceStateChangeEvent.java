package pl.temomuko.autostoprace.data.event;

/**
 * Created by Rafał Naniewicz on 13.03.2016.
 */
public class PostServiceStateChangeEvent {

    private boolean mIsPostServiceActive;

    public PostServiceStateChangeEvent(boolean isPostServiceActive) {
        mIsPostServiceActive = isPostServiceActive;
    }

    public boolean isPostServiceActive() {
        return mIsPostServiceActive;
    }
}
