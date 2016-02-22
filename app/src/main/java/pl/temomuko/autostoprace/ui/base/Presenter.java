package pl.temomuko.autostoprace.ui.base;

/**
 * Created by Szymon Kozak on 2016-01-09.
 */
public interface Presenter<T extends MvpView> {

    void attachView(T view);

    void detachView();
}
