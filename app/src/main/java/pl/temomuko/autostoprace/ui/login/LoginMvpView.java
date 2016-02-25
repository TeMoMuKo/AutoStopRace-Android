package pl.temomuko.autostoprace.ui.login;

import pl.temomuko.autostoprace.ui.base.MvpView;

/**
 * Created by Szymon Kozak on 2016-01-22.
 */
public interface LoginMvpView extends MvpView {

    void startMainActivity();

    void showInvalidEmailValidaionError();

    void showEmptyPasswordValidationError();

    void hideEmailValidationError();

    void hidePasswordValidationError();

    void showError(String message);

    void setProgress(boolean state);

    // void saveCurrentRequestObservable(Observable<Response<SignInResponse>> observable);
}
