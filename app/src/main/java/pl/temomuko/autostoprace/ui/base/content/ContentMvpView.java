package pl.temomuko.autostoprace.ui.base.content;

import pl.temomuko.autostoprace.ui.base.MvpView;

/**
 * Created by szymen on 2016-01-30.
 */
public interface ContentMvpView extends MvpView {

    void showError(String message);
}
