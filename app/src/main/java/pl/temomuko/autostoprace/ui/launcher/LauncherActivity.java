package pl.temomuko.autostoprace.ui.launcher;

import android.content.Intent;
import android.os.Bundle;
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
    @Bind(R.id.btn_go_to_login) Button mGoToLoginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        getActivityComponent().inject(this);
        mLauncherPresenter.attachView(this);
        setListeners();
    }

    public void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void setListeners() {
        mGoToLoginButton.setOnClickListener(v -> goToLoginActivity());
    }
}
