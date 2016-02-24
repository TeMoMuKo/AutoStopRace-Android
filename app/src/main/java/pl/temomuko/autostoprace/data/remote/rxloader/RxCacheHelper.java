package pl.temomuko.autostoprace.data.remote.rxloader;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;

import rx.Observable;

/**
 * Created by Szymon Kozak on 2016-02-24.
 */
public class RxCacheHelper<T> {

    private static final String TAB_RETAINED_FRAGMENT = "tab_retained_fragment";
    private RxRetainedFragment mRetainedFragment;

    public RxCacheHelper(Activity activity) {
        setupRetainedFragment(activity);
    }

    private void setupRetainedFragment(Activity activity) {
        FragmentManager fm = activity.getFragmentManager();
        Fragment fragment = fm.findFragmentByTag(TAB_RETAINED_FRAGMENT);
        mRetainedFragment = (RxRetainedFragment) fragment;
        if (fragment == null) {
            mRetainedFragment = new RxRetainedFragment();
            fm.beginTransaction().add(mRetainedFragment, TAB_RETAINED_FRAGMENT).commit();
        }
    }

    @SuppressWarnings("unchecked")
    public void saveObservable(Observable<T> observable) {
        mRetainedFragment.setCurrentRequestObservable(observable);
    }

    @SuppressWarnings("unchecked")
    public Observable<T> getSavedObservable() {
        return mRetainedFragment.getCurrentRequestObservable();
    }
}
