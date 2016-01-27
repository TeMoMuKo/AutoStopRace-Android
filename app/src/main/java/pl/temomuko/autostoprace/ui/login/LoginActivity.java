package pl.temomuko.autostoprace.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.Bind;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.BaseActivity;
import pl.temomuko.autostoprace.ui.main.MainActivity;

/**
 * Created by szymen on 2016-01-22.
 */
public class LoginActivity extends BaseActivity implements LoginMvpView {

    @Inject LoginPresenter mLoginPresenter;
    @Bind(R.id.et_email) EditText mEmailEditText;
    @Bind(R.id.et_password) EditText mPasswordEditText;
    @Bind(R.id.btn_login) Button mLoginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getActivityComponent().inject(this);
        mLoginPresenter.attachView(this);
        setListeners();
    }

    private void setListeners() {
        mLoginButton.setOnClickListener(v -> {
            String email = mEmailEditText.getText().toString();
            String password = mPasswordEditText.getText().toString();
            mLoginPresenter.signIn(email, password);
        });
    }

    @Override
    public void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void showApiError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
