package pl.temomuko.autostoprace.ui.login;

import pl.temomuko.autostoprace.ui.base.MvpView;

/**
 * Created by Szymon Kozak on 2016-01-22.
 */
public interface LoginMvpView extends MvpView {

    void startMainActivity();

    void setInvalidEmailValidationError(boolean state);

    void setInvalidPasswordValidationError(boolean state);

    void showError(String message);

    void setProgress(boolean state);
}
