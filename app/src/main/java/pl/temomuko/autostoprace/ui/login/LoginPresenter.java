package pl.temomuko.autostoprace.ui.login;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.ui.base.BasePresenter;

/**
 * Created by szymen on 2016-01-22.
 */
public class LoginPresenter extends BasePresenter<LoginMvpView> {

    private DataManager mDataManager;

    @Inject
    public LoginPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }
}
