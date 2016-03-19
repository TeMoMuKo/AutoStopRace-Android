package pl.temomuko.autostoprace.ui.login.resetpass;

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
import pl.temomuko.autostoprace.ui.base.BaseActivity;
import pl.temomuko.autostoprace.util.DialogFactory;
import pl.temomuko.autostoprace.util.rx.RxCacheHelper;

/**
 * Created by Szymon Kozak on 2016-03-19.
 */
public class ResetPassActivity extends BaseActivity implements ResetPassMvpView {

    private static final String TAG = ResetPassActivity.class.getSimpleName();
    private static final String BUNDLE_IS_PROGRESS_DIALOG_SHOWN = "bundle_is_progress_dialog_shown";

    @Inject ResetPassPresenter mResetPassPresenter;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.til_email) TextInputLayout mEmailTextInputLayout;
    @Bind(R.id.et_email) EditText mEmailEditText;
    @Bind(R.id.btn_reset) Button mResetButton;
    private MaterialDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        getActivityComponent().inject(this);
        mResetPassPresenter.setupRxCacheHelper(this, RxCacheHelper.get(TAG));
        mResetPassPresenter.attachView(this);
        createProgressDialog();
        loadProgressDialogState(savedInstanceState);
        setupToolbarWithBack();
        setListeners();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        saveProgressDialogState(outState);
        super.onSaveInstanceState(outState);
    }

    private void saveProgressDialogState(Bundle outState) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            outState.putBoolean(BUNDLE_IS_PROGRESS_DIALOG_SHOWN, true);
        } else {
            outState.putBoolean(BUNDLE_IS_PROGRESS_DIALOG_SHOWN, false);
        }
    }

    private void createProgressDialog() {
        mProgressDialog = DialogFactory.createResetPassProcessDialog(this, mResetPassPresenter);
    }

    private void setListeners() {
        mResetButton.setOnClickListener(v -> {
            String email = mEmailEditText.getText().toString();
            mResetPassPresenter.resetPassword(email);
        });
    }

    private void loadProgressDialogState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(BUNDLE_IS_PROGRESS_DIALOG_SHOWN)) {
                mProgressDialog.show();
            }
        }
    }

    private void setupToolbarWithBack() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /* MVP View Methods */

    @Override
    public void showSuccessInfo(String email) {
        Toast.makeText(this, getString(R.string.msg_reset_pass_email_sent, email),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void setProgress(boolean state) {
        if (state) mProgressDialog.show();
        else mProgressDialog.dismiss();
    }

    public void setInvalidEmailValidationError(boolean state) {
        mEmailTextInputLayout.setErrorEnabled(state);
        mEmailTextInputLayout.setError(state ? getString(R.string.error_invalid_email) : null);
    }
}
