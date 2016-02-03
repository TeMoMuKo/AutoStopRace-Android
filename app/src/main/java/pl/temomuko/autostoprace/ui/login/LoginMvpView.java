package pl.temomuko.autostoprace.ui.login;

import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.ui.base.content.ContentMvpView;
import retrofit2.Response;
import rx.Observable;

/**
 * Created by szymen on 2016-01-22.
 */
public interface LoginMvpView extends ContentMvpView {

    void startMainActivity();

    void showEmailValidationError(String message);

    void showPasswordValidationError(String message);

    void hideEmailValidationError();

    void hidePasswordValidationError();

    void saveCurrentRequestObservable(Observable<Response<SignInResponse>> observable);
}
