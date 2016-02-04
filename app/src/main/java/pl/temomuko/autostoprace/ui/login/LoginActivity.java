package pl.temomuko.autostoprace.ui.login;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import javax.inject.Inject;

import butterknife.Bind;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.ui.base.BaseActivity;
import pl.temomuko.autostoprace.ui.main.MainActivity;
import retrofit2.Response;
import rx.Observable;

/**
 * Created by szymen on 2016-01-22.
 */
public class LoginActivity extends BaseActivity implements LoginMvpView {

    @Inject LoginPresenter mLoginPresenter;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.et_email) EditText mEmailEditText;
    @Bind(R.id.et_password) EditText mPasswordEditText;
    @Bind(R.id.btn_login) Button mLoginButton;
    @Bind(R.id.til_email) TextInputLayout mEmailTextInputLayout;
    @Bind(R.id.til_password) TextInputLayout mPasswordTextInputLayout;
    private MaterialDialog mProgressDialog;
    private RetainedLoginFragment mRetainedLoginFragment;
    private static final String TAG_LOGIN_FRAGMENT = "tag_login_fragment";
    private static final String BUNDLE_IS_PROGRESS_DIALOG_SHOWN = "bundle_is_progress_dialog_shown";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getActivityComponent().inject(this);
        setupRetainedLoginFragment();
        mLoginPresenter.setCurrentRequestObservable(mRetainedLoginFragment.getCurrentRequestObservable());
        mLoginPresenter.attachView(this);
        setupToolbar();
        setListeners();
        buildProgressDialog();
        setupProgressDialog(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        mLoginPresenter.detachView();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        checkProgressDialog(outState);
        super.onSaveInstanceState(outState);
    }

    private void checkProgressDialog(Bundle outState) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            outState.putBoolean(BUNDLE_IS_PROGRESS_DIALOG_SHOWN, true);
        } else {
            outState.putBoolean(BUNDLE_IS_PROGRESS_DIALOG_SHOWN, false);
        }
    }

    private void setupRetainedLoginFragment() {
        FragmentManager fm = getFragmentManager();
        mRetainedLoginFragment = (RetainedLoginFragment) fm.findFragmentByTag(TAG_LOGIN_FRAGMENT);
        if (mRetainedLoginFragment == null) {
            mRetainedLoginFragment = new RetainedLoginFragment();
            fm.beginTransaction().add(mRetainedLoginFragment, TAG_LOGIN_FRAGMENT).commit();
        }
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setListeners() {
        mLoginButton.setOnClickListener(v -> {
            String email = mEmailEditText.getText().toString().trim();
            String password = mPasswordEditText.getText().toString();
            mLoginPresenter.signIn(email, password);
        });
    }

    private void buildProgressDialog() {
        mProgressDialog = new MaterialDialog.Builder(this)
                .title(R.string.title_logging)
                .content(R.string.please_wait)
                .cancelListener(dialog -> mLoginPresenter.cancelSignInRequest())
                .progress(true, 0)
                .build();
    }

    private void setupProgressDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(BUNDLE_IS_PROGRESS_DIALOG_SHOWN)) {
                mProgressDialog.show();
            }
        }
    }

    @Override
    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void setProgress(boolean status) {
        if (status) mProgressDialog.show();
        else mProgressDialog.dismiss();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showEmailValidationError(String message) {
        mEmailTextInputLayout.setErrorEnabled(true);
        mEmailTextInputLayout.setError(message);
    }

    @Override
    public void showPasswordValidationError(String message) {
        mPasswordTextInputLayout.setErrorEnabled(true);
        mPasswordTextInputLayout.setError(message);
    }

    @Override
    public void hideEmailValidationError() {
        mEmailTextInputLayout.setErrorEnabled(false);
    }

    @Override
    public void hidePasswordValidationError() {
        mPasswordTextInputLayout.setErrorEnabled(false);
    }

    @Override
    public void saveCurrentRequestObservable(Observable<Response<SignInResponse>> observable) {
        mRetainedLoginFragment.setCurrentRequestObservable(observable);
    }
}
