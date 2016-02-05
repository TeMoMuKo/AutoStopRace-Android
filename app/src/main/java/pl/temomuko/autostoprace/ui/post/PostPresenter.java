package pl.temomuko.autostoprace.ui.post;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.Location;
import pl.temomuko.autostoprace.ui.base.BasePresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
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

    public void backToMain() {
        getMvpView().onBackPressed();
    }

    public void saveLocation(String message) {
        double latitude = 12.34;
        double longitude = 43.21;
        Location locationToSend = new Location(latitude, longitude, message);
        mDataManager.saveUnsentLocationToDatabase(locationToSend);
    }
}
