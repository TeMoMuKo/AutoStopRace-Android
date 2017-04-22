package pl.temomuko.autostoprace.ui.login.resetpass;

import pl.temomuko.autostoprace.ui.base.MvpView;

/**
 * Created by Szymon Kozak on 2016-03-19.
 */
public interface ResetPassMvpView extends MvpView {

    void setInvalidEmailValidationError(boolean state);

    void showSuccessInfo();

    void showError(String message);

    void setProgress(boolean state);

    void finish();
}
