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
    private Observable<T> mCachedObservable;

    public RxCacheHelper(Activity activity) {
        setupRetainedFragment(activity);
    }

    public static <T> RxCacheHelper<T> create(Activity activity) {
        return new RxCacheHelper<>(activity);
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
    public void save() {
        mRetainedFragment.setCurrentRequestObservable(mCachedObservable);
    }

    @SuppressWarnings("unchecked")
    public void restore() {
        mCachedObservable = mRetainedFragment.getCurrentRequestObservable();
    }

    public void cache(Observable<T> observable) {
        mCachedObservable = observable.cache();
    }

    public void clear() {
        mCachedObservable = null;
    }

    public Observable<T> getCachedObservable() {
        return mCachedObservable;
    }
}
