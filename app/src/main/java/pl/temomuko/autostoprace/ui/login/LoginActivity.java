package pl.temomuko.autostoprace.ui.login;

import android.content.Intent;
import android.os.Bundle;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.BaseActivity;
import pl.temomuko.autostoprace.ui.main.MainActivity;

/**
 * Created by szymen on 2016-01-22.
 */
public class LoginActivity extends BaseActivity implements LoginMvpView {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    public void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showError() {
        //TODO
    }
}
