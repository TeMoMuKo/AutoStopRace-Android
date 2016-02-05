package pl.temomuko.autostoprace.ui.post;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.CreateLocationRequest;
import pl.temomuko.autostoprace.data.model.Location;
import pl.temomuko.autostoprace.ui.base.content.ContentPresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.HttpStatus;
import pl.temomuko.autostoprace.util.RxUtil;
import retrofit2.Response;
import rx.Subscription;

/**
 * Created by szymen on 2016-01-30.
 */
public class PostPresenter extends ContentPresenter<PostMvpView> {

    private Subscription mSubscription;
    private final static String TAG = "PostPresenter";

    @Inject
    public PostPresenter(DataManager dataManager, ErrorHandler errorHandler) {
        super(errorHandler, dataManager);
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
        double logitude = 43.21;
        Location locationToSend = new Location(latitude, logitude, message);
        mDataManager.saveUnsentLocationToDatabase(locationToSend);
        CreateLocationRequest request = new CreateLocationRequest(locationToSend);
        mSubscription = mDataManager.postLocationToServer(request)
                .compose(RxUtil.applySchedulers())
                .subscribe(this::processLocationsResponse, this::handleError);
    }

    private void processLocationsResponse(Response<Location> response) {
        if (response.code() == HttpStatus.CREATED) {
            getMvpView().showSuccessInfo();
            getMvpView().startMainActivity();
        } else if (response.code() == HttpStatus.UNAUTHORIZED) {
            getMvpView().showSessionExpiredError();
            getMvpView().startLoginActivity();
        } else {
            handleStandardResponseError(response);
        }
    }
}
