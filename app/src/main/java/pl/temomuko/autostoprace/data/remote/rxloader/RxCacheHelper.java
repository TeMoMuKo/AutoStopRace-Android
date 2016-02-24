package pl.temomuko.autostoprace.data.remote.rxloader;

import android.app.Activity;
import android.app.FragmentManager;

import pl.temomuko.autostoprace.data.model.SignInResponse;
import retrofit2.Response;
import rx.Observable;

/**
 * Created by Szymon Kozak on 2016-02-24.
 */
public class RxCacheHelper {

    private static final String TAB_RETAINED_FRAGMENT = "tab_retained_fragment";
    private RxRetainedFragment mRetainedFragment;

    public RxCacheHelper(Activity activity) {
        setupRetainedFragment(activity);
    }

    private void setupRetainedFragment(Activity activity) {
        FragmentManager fm = activity.getFragmentManager();
        mRetainedFragment = (RxRetainedFragment) fm.findFragmentByTag(TAB_RETAINED_FRAGMENT);
        if (mRetainedFragment == null) {
            mRetainedFragment = new RxRetainedFragment();
            fm.beginTransaction().add(mRetainedFragment, TAB_RETAINED_FRAGMENT).commit();
        }
    }

    public void saveObservable(Observable<Response<SignInResponse>> observable) {
        mRetainedFragment.setCurrentRequestObservable(observable);
    }

    public Observable<Response<SignInResponse>> getSavedObservable() {
        return mRetainedFragment.getCurrentRequestObservable();
    }
}
