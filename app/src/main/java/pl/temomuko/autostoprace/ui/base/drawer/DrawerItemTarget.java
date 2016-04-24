package pl.temomuko.autostoprace.ui.base.drawer;

import pl.temomuko.autostoprace.ui.base.BaseActivity;

/**
 * Created by Szymon Kozak on 2016-02-11.
 */
public class DrawerItemTarget {

    private final Class<? extends BaseActivity> mTargetActivityClass;
    private final int mTargetActivityId;

    public DrawerItemTarget(Class<? extends BaseActivity> targetActivityClass, int targetActivityId) {
        mTargetActivityClass = targetActivityClass;
        mTargetActivityId = targetActivityId;
    }

    public Class<? extends BaseActivity> getActivityClass() {
        return mTargetActivityClass;
    }

    public int getActivityId() {
        return mTargetActivityId;
    }
}