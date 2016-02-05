package pl.temomuko.autostoprace.ui.login;

import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.ui.base.MvpView;
import retrofit2.Response;
import rx.Observable;

/**
 * Created by szymen on 2016-01-22.
 */
public interface LoginMvpView extends MvpView {

    void startMainActivity();

    void showEmailValidationError(String message);

    void showPasswordValidationError(String message);

    void hideEmailValidationError();

    void hidePasswordValidationError();

    void showError(String message);

    void setProgress(boolean state);

    void saveCurrentRequestObservable(Observable<Response<SignInResponse>> observable);
}
