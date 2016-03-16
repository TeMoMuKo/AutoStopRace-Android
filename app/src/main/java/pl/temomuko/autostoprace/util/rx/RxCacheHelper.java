package pl.temomuko.autostoprace.util.rx;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by Szymon Kozak on 2016-02-24.
 */

/* Helps keep any request observable
to continue it after orientation change */

public class RxCacheHelper<T> {

    private static final String FRAGMENT_TAG_SUFFIX = RxCacheHelper.class.getName();
    private static List<RxCacheHelper> sHelpers;

    private RxRetainedFragment mRetainedFragment;
    private String mTag;
    private OnContinueListener mOnContinueListener;

    /**
     * @return Instance of RxCacheHelper by individual tag,
     * if instance is null, this create new object
     */
    @SuppressWarnings("unchecked")
    public static <T> RxCacheHelper<T> get(String tag) {
        if (sHelpers == null) sHelpers = new ArrayList<>();
        for (RxCacheHelper<T> helper : sHelpers) {
            if (helper.getTag().equals(tag)) return helper;
        }
        RxCacheHelper helper = new RxCacheHelper<T>();
        helper.setTag(tag);
        sHelpers.add(helper);
        return helper;
    }

    private RxCacheHelper() {
    }

    private void setTag(String tag) {
        mTag = tag;
    }

    private String getTag() {
        return mTag;
    }

    /**
     * Setup helper with activity which provides FragmentManager
     */
    public void setup(Activity activity) {
        setupRetainedFragment(activity);
    }

    private void setupRetainedFragment(Activity activity) {
        FragmentManager fm = activity.getFragmentManager();
        Fragment fragment = fm.findFragmentByTag(mTag + FRAGMENT_TAG_SUFFIX);
        mRetainedFragment = (RxRetainedFragment) fragment;
        if (fragment == null) {
            mRetainedFragment = new RxRetainedFragment();
            fm.beginTransaction().add(mRetainedFragment, mTag + FRAGMENT_TAG_SUFFIX).commit();
        }
    }

    /**
     * Cache observable and allows restore it after configuration change.
     */
    @SuppressWarnings("unchecked")
    public RxCacheHelper cache(Observable<T> observable) {
        if (mRetainedFragment == null) throw new RuntimeException("First setup RxCacheHelper.");
        mRetainedFragment.setCurrentObservable(observable.cache());
        return this;
    }

    /**
     * Restore cached observable.
     * Use after {@link #cache(Observable) } method.
     *
     * @return cached observable or null when cache is empty.
     */
    @SuppressWarnings("unchecked")
    public Observable<T> getRestoredCachedObservable() {
        return mRetainedFragment.getCurrentObservable();
    }

    /**
     * Setup optional listener if you want use {@link #continueRequest()}.
     */
    public void onContinue(OnContinueListener onContinueListener) {
        mOnContinueListener = onContinueListener;
    }

    /**
     * Continue action saved in {@link #onContinue(OnContinueListener)} ()}.
     */
    public void continueRequest() {
        if(mOnContinueListener != null) {
            mOnContinueListener.onContinue();
        }
    }

    /**
     * Clear cache when you need to cancel current request.
     */
    public void clearCache() {
        mRetainedFragment.clearCurrentObservable();
    }

    public boolean isCached() {
        return mRetainedFragment.getCurrentObservable() != null;
    }

    public static class RxRetainedFragment<T> extends Fragment {

        private Observable<T> mCurrentObservable;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        public void setCurrentObservable(Observable<T> observable) {
            mCurrentObservable = observable;
        }

        public Observable<T> getCurrentObservable() {
            return mCurrentObservable;
        }

        public void clearCurrentObservable() {
            mCurrentObservable = null;
        }
    }

    public static interface OnContinueListener {
        void onContinue();
    }
}
