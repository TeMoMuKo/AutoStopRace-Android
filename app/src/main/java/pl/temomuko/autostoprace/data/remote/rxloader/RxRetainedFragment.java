package pl.temomuko.autostoprace.data.remote.rxloader;

import android.app.Fragment;
import android.os.Bundle;

import pl.temomuko.autostoprace.data.model.SignInResponse;
import retrofit2.Response;
import rx.Observable;

/**
 * Created by Szymon Kozak on 2016-02-24.
 */
public class RxRetainedFragment extends Fragment {


    private Observable<Response<SignInResponse>> mCurrentRequestObservable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setCurrentRequestObservable(Observable<Response<SignInResponse>> observable) {
        mCurrentRequestObservable = observable;
    }

    public Observable<Response<SignInResponse>> getCurrentRequestObservable() {
        return mCurrentRequestObservable;
    }

}