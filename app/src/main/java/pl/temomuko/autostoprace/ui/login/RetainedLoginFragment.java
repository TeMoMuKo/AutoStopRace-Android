package pl.temomuko.autostoprace.ui.login;

import android.app.Fragment;
import android.os.Bundle;

import pl.temomuko.autostoprace.data.model.SignInResponse;
import retrofit2.Response;
import rx.Observable;

/**
 * Created by szymen on 2016-02-03.
 */

/* Fragment which helps keep SignIn Observable
to continue same request after orientation change */

public class RetainedLoginFragment extends Fragment {

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
