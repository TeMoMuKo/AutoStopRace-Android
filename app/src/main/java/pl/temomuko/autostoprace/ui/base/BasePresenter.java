package pl.temomuko.autostoprace.ui.base;

/**
 * Created by szymen on 2016-01-09.
 */
public class BasePresenter<V extends MvpView> implements Presenter<V> {

    private V mMvpView;

    @Override
    public void attachView(V mvpView) {
        mMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mMvpView = null;
    }

    public boolean isViewAttached() {
        return mMvpView != null;
    }

    public V getMvpView() {
        return mMvpView;
    }
}
