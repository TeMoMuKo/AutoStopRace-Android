package pl.temomuko.autostoprace.ui.main;

import android.content.Context;

import java.util.ArrayList;

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

    public void loadLocationsFromDatabase() {
        //TODO
    }

    public void loadLocationsFromServer() {
        mSubscription = mDataManager.getTeamLocationsFromServer()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.newThread())
                .onErrorReturn(throwable -> {
                    handleLoadLocationsError(throwable);
                    return new ArrayList<>();
                })
                .switchMap(mDataManager::saveLocationsToDatabase)
                .subscribe(locations -> {
                    if(locations.isEmpty()) getMvpView().showEmptyInfo();
                    else getMvpView().updateLocationsList(locations);
                });
    }

    private void handleLoadLocationsError(Throwable throwable) {
        ApiErrorResponse response = ApiErrorResponse.create(throwable);
        Context context = (Context) getMvpView();
        switch (response.getStatus()) {
            case 404:
                getMvpView().showApiError(context.getString(R.string.error_404));
                break;
            case 403:
                getMvpView().showApiError(context.getString(R.string.error_403));
                break;
            case 500:
                getMvpView().showApiError(context.getString(R.string.error_500));
                break;
            default:
                getMvpView().showApiError(context.getString(R.string.error_unknown));
        }
    }
}
