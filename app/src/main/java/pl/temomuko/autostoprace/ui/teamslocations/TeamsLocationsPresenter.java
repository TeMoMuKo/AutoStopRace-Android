package pl.temomuko.autostoprace.ui.teamslocations;

import java.util.List;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.remote.HttpStatus;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerBasePresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.LogUtil;
import pl.temomuko.autostoprace.util.rx.RxUtil;
import retrofit2.Response;
import rx.Subscription;

/**
 * Created by Rafał Naniewicz on 01.04.2016.
 */
public class TeamsLocationsPresenter extends DrawerBasePresenter<TeamsLocationsMvpView> {

    private final static String TAG = TeamsLocationsPresenter.class.getSimpleName();
    private final ErrorHandler mErrorHandler;
    private Subscription mSubscription;

    @Inject
    public TeamsLocationsPresenter(DataManager dataManager, ErrorHandler errorHandler) {
        super(dataManager);
        mErrorHandler = errorHandler;
    }

    @Override
    public void attachView(TeamsLocationsMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        if (mSubscription != null) mSubscription.unsubscribe();
        super.detachView();
    }

    public void handleTeamCharSequence(CharSequence charSequence) {
        int teamId = Integer.valueOf(charSequence.toString());
        getMvpView().displayTeam(teamId);
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
        getMvpView().showError(throwable.toString());
        LogUtil.e(TAG, throwable.toString());
    }
}
