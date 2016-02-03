package pl.temomuko.autostoprace.ui.base.content;

import android.content.Context;

import java.io.IOException;
import java.net.SocketTimeoutException;

import javax.inject.Inject;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.ui.base.BasePresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.NetworkUtil;
import retrofit2.Response;

/**
 * Created by szymen on 2016-01-30.
 */
public class ContentPresenter<T extends ContentMvpView> extends BasePresenter<T> {

    protected ErrorHandler mErrorHandler;
    protected DataManager mDataManager;

    @Inject
    public ContentPresenter(ErrorHandler errorHandler, DataManager dataManager) {
        mErrorHandler = errorHandler;
        mDataManager = dataManager;
    }

    protected void handleStandardResponseError(Response response) {
        getMvpView().showError(mErrorHandler.getMessage(response));
    }

    public void handleError(Throwable throwable) {
        getMvpView().setProgress(false);
        Context context = (Context) getMvpView();
        if((throwable instanceof SocketTimeoutException)) {
            getMvpView().showError(context.getString(R.string.error_timeout));
        } else if ((throwable instanceof IOException) && !NetworkUtil.isConnected(context)) {
            getMvpView().showError(context.getString(R.string.error_no_internet_connection));
        } else {
            getMvpView().showError(context.getString(R.string.error_unknown));
        }
    }
}
