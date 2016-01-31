package pl.temomuko.autostoprace.ui.post;

import pl.temomuko.autostoprace.ui.base.content.ContentMvpView;

/**
 * Created by szymen on 2016-01-30.
 */
public interface PostMvpView extends ContentMvpView {

    void startMainActivity();

    void startLoginActivity();

    void showSuccessInfo();

    void onBackPressed();

    void showSessionExpiredError();
}
