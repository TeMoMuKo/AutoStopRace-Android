package pl.temomuko.autostoprace.ui.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import javax.inject.Inject;

import butterknife.Bind;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerActivity;
import pl.temomuko.autostoprace.ui.contact.ContactActivity;
import pl.temomuko.autostoprace.ui.login.LoginActivity;

/**
 * Created by szymen on 2016-01-22.
 */
public class LauncherActivity extends DrawerActivity implements LauncherMvpView {

    @Inject LauncherPresenter mLauncherPresenter;
    @Bind(R.id.btn_go_to_login) Button mGoToLoginButton;
    @Bind(R.id.btn_go_to_contact) Button mGoToContactButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        getActivityComponent().inject(this);
        mLauncherPresenter.attachView(this);
        setupToolbarWithToggle();
        setListeners();
    }

    @Override
    protected void onDestroy() {
        mLauncherPresenter.detachView();
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    private void setListeners() {
        mGoToLoginButton.setOnClickListener(v -> mLauncherPresenter.goToLogin());
        mGoToContactButton.setOnClickListener(v -> mLauncherPresenter.goToContact());
    }

    @Override
    public void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void startContactActivity() {
        Intent intent = new Intent(this, ContactActivity.class);
        startActivity(intent);
    }
}
