package pl.temomuko.autostoprace.data.remote.rxloader;

import android.app.Fragment;
import android.os.Bundle;

import rx.Observable;

/**
 * Created by Szymon Kozak on 2016-02-24.
 */
public class RxRetainedFragment<T> extends Fragment {

    private Observable<T> mCurrentRequestObservable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setCurrentRequestObservable(Observable<T> observable) {
        mCurrentRequestObservable = observable;
    }

    public Observable<T> getCurrentRequestObservable() {
        return mCurrentRequestObservable;
    }
}