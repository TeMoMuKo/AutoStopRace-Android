package pl.temomuko.autostoprace.ui.launcher;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.ui.base.BasePresenter;
import rx.Subscription;

/**
 * Created by szymen on 2016-01-22.
 */
public class LauncherPresenter extends BasePresenter<LauncherMvpView> {

    private Subscription mSubscription;
    private DataManager mDataManager;

    @Inject
    public LauncherPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(LauncherMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null && !mSubscription.isUnsubscribed()) mSubscription.unsubscribe();
    }
}
