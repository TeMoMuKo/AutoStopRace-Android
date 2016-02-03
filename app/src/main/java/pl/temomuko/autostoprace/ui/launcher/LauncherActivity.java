package pl.temomuko.autostoprace.ui.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import javax.inject.Inject;

import butterknife.Bind;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.BaseActivity;
import pl.temomuko.autostoprace.ui.login.LoginActivity;

/**
 * Created by szymen on 2016-01-22.
 */
public class LauncherActivity extends BaseActivity implements LauncherMvpView {

    @Inject LauncherPresenter mLauncherPresenter;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.btn_go_to_login) Button mGoToLoginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        getActivityComponent().inject(this);
        mLauncherPresenter.attachView(this);
        setupToolbar();
        setListeners();
    }

    @Override
    protected void onDestroy() {
        mLauncherPresenter.detachView();
        super.onDestroy();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
    }

    private void setListeners() {
        mGoToLoginButton.setOnClickListener(v -> mLauncherPresenter.goToLogin());
    }

    @Override
    public void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
