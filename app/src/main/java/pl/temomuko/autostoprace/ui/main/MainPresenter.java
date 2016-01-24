package pl.temomuko.autostoprace.ui.main;

import android.content.Context;

import javax.inject.Inject;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.ApiErrorResponse;
import pl.temomuko.autostoprace.ui.base.BasePresenter;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by szymen on 2016-01-09.
 */
public class MainPresenter extends BasePresenter<MainMvpView> {

    private DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public MainPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(MainMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null && !mSubscription.isUnsubscribed()) mSubscription.unsubscribe();
    }

    public void loadLocationsFromApi() {
        mSubscription = mDataManager.getCurrentUserTeamLocations()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.newThread())
                .subscribe(locations -> {
                    getMvpView().updateLocationsList(locations);
                }, this::handleLoadLocationsError);
    }

    public void handleLoadLocationsError(Throwable throwable) {
        ApiErrorResponse response = ApiErrorResponse.create(throwable);
        Context context = (Context) getMvpView();
        switch (response.getStatus()) {
            case 404:
                getMvpView().showError(context.getString(R.string.error_404));
                break;
            case 403:
                getMvpView().showError(context.getString(R.string.error_403));
                break;
            case 500:
                getMvpView().showError(context.getString(R.string.error_500));
                break;
            default:
                getMvpView().showError(context.getString(R.string.error_unknown));
        }
    }
}
