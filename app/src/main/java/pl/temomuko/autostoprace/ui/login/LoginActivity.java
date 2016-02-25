package pl.temomuko.autostoprace.ui.login;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import javax.inject.Inject;

import butterknife.Bind;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.util.rx.RxCacheHelper;
import pl.temomuko.autostoprace.ui.base.BaseActivity;
import pl.temomuko.autostoprace.ui.main.MainActivity;
import pl.temomuko.autostoprace.util.DialogFactory;

/**
 * Created by Szymon Kozak on 2016-01-22.
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
    private DialogFragment mHelpDialogFragment;
    private static final String TAG_HELP_DIALOG_FRAGMENT = "help_dialog_fragment";
    private static final String BUNDLE_IS_PROGRESS_DIALOG_SHOWN = "bundle_is_progress_dialog_shown";
    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getActivityComponent().inject(this);
        mLoginPresenter.setupRxCacheHelper(this, RxCacheHelper.get(TAG));
        mLoginPresenter.attachView(this);
        createProgressDialog();
        createHelpDialog();
        loadProgressDialogState(savedInstanceState);
        setupToolbarWithBack();
        setListeners();
    }

    @Override
    protected void onDestroy() {
        mLoginPresenter.detachView();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        saveProgressDialogState(outState);
        super.onSaveInstanceState(outState);
    }

    private void setListeners() {
        mLoginButton.setOnClickListener(v -> {
            String email = mEmailEditText.getText().toString().trim();
            String password = mPasswordEditText.getText().toString();
            mLoginPresenter.signIn(email, password);
        });
    }

    private void saveProgressDialogState(Bundle outState) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            outState.putBoolean(BUNDLE_IS_PROGRESS_DIALOG_SHOWN, true);
        } else {
            outState.putBoolean(BUNDLE_IS_PROGRESS_DIALOG_SHOWN, false);
        }
    }

    private void setupToolbarWithBack() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_login_help:
                mHelpDialogFragment.show(getFragmentManager(),
                        TAG_HELP_DIALOG_FRAGMENT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createHelpDialog() {
        mHelpDialogFragment = DialogFactory.HelpDialogFragment.create();
    }

    private void createProgressDialog() {
        mProgressDialog = DialogFactory.createLoggingProcessDialog(this, mLoginPresenter);
    }

    private void loadProgressDialogState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(BUNDLE_IS_PROGRESS_DIALOG_SHOWN)) {
                mProgressDialog.show();
            }
        }
    }

    /* MVP View methods */

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
    public void showInvalidEmailValidaionError() {
        mEmailTextInputLayout.setErrorEnabled(true);
        mEmailTextInputLayout.setError(getString(R.string.error_invalid_email));
    }

    @Override
    public void showEmptyPasswordValidationError() {
        mPasswordTextInputLayout.setErrorEnabled(true);
        mPasswordTextInputLayout.setError(getString(R.string.error_empty_pass));
    }

    @Override
    public void hideEmailValidationError() {
        mEmailTextInputLayout.setErrorEnabled(false);
    }

    @Override
    public void hidePasswordValidationError() {
        mPasswordTextInputLayout.setErrorEnabled(false);
    }
}
