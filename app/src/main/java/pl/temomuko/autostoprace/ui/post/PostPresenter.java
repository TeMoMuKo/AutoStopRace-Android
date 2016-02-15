package pl.temomuko.autostoprace.ui.post;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.ui.base.BasePresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.RxUtil;
import rx.Subscription;

/**
 * Created by szymen on 2016-01-30.
 */
public class PostPresenter extends BasePresenter<PostMvpView> {

    private DataManager mDataManager;
    private ErrorHandler mErrorHandler;
    private Subscription mSubscription;
    private final static String TAG = "PostPresenter";

    @Inject
    public PostPresenter(DataManager dataManager, ErrorHandler errorHandler) {
        mDataManager = dataManager;
        mErrorHandler = errorHandler;
    }

    @Override
    public void attachView(PostMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        if (mSubscription != null && !mSubscription.isUnsubscribed()) mSubscription.unsubscribe();
        super.detachView();
    }

    public void saveLocation(String message) {
        double latitude = 51.12345;
        double longitude = 21.12345;
        LocationRecord locationRecordToSend = new LocationRecord(latitude, longitude, message);
        mSubscription = mDataManager.saveUnsentLocationRecordToDatabase(locationRecordToSend)
                .compose(RxUtil.applySchedulers())
                .subscribe();
        getMvpView().showSuccessInfo();
        getMvpView().startMainActivity();
    }

    public void setupCurrentLocation() {
        getMvpView().updateCurrentLocationAddress("ul. Sezamkowa 12, Wrocław, Polska");
        getMvpView().updateCurrentLocationCords(51.12345, 21.12345);
    }
}
