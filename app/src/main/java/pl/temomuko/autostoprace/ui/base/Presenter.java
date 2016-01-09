package pl.temomuko.autostoprace.ui.base;

/**
 * Created by szymen on 2016-01-09.
 */
public interface Presenter<V extends MvpView> {

    void attachView(V view);

    void detachView();
}
