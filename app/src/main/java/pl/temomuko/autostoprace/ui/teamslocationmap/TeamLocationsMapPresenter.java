package pl.temomuko.autostoprace.ui.teamslocationmap;

import java.util.List;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.remote.HttpStatus;
import pl.temomuko.autostoprace.ui.base.BasePresenter;
import pl.temomuko.autostoprace.data.remote.ErrorHandler;
import pl.temomuko.autostoprace.util.LogUtil;
import pl.temomuko.autostoprace.util.rx.RxUtil;
import retrofit2.Response;
import rx.Subscription;

/**
 * Created by Rafał Naniewicz on 03.04.2016.
 */
public class TeamLocationsMapPresenter extends BasePresenter<TeamLocationsMapMvpView> {

    private static final String TAG = TeamLocationsMapPresenter.class.getSimpleName();

    private DataManager mDataManager;
    private ErrorHandler mErrorHandler;

    private Subscription mSubscription;

    @Inject
    public TeamLocationsMapPresenter(DataManager dataManager, ErrorHandler errorHandler) {
        mDataManager = dataManager;
        mErrorHandler = errorHandler;
    }

    @Override
    public void attachView(TeamLocationsMapMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        if (mSubscription != null) mSubscription.unsubscribe();
        super.detachView();
    }

    public void loadTeam(int teamId) {
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getTeamLocationRecordsFromServer(teamId)
                .flatMap(HttpStatus::requireOk)
                .map(Response::body)
                .compose(RxUtil.applyIoSchedulers())
                .subscribe(this::setLocations,
                        this::handleError);
    }

    private void setLocations(List<LocationRecord> locations) {
        getMvpView().setLocations(locations);
    }

    private void handleError(Throwable throwable) {
        //// TODO: 04.04.2016
        getMvpView().showError(throwable.toString());
        LogUtil.e(TAG, throwable.toString());
    }
}
