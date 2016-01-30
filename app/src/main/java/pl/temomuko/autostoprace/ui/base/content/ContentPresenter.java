package pl.temomuko.autostoprace.ui.base.content;

import android.content.Context;

import java.io.IOException;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.BasePresenter;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.NetworkUtil;
import retrofit.Response;

/**
 * Created by szymen on 2016-01-30.
 */
public class ContentPresenter<T extends ContentMvpView> extends BasePresenter<T> {

    protected void handleResponseError(Response response) {
        Context context = (Context) getMvpView();
        ErrorHandler handler = new ErrorHandler(context, response);
        getMvpView().showError(handler.getMessage());
    }

    public void handleError(Throwable throwable) {
        Context context = (Context) getMvpView();
        if ((throwable instanceof IOException) && !NetworkUtil.isConnected(context)) {
            getMvpView().showError(context.getString(R.string.error_no_internet_connection));
        } else {
            getMvpView().showError(context.getString(R.string.error_unknown));
        }
    }
}
