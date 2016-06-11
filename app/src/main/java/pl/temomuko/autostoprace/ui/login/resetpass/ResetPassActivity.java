package pl.temomuko.autostoprace.ui.login.resetpass;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.BindView;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.BaseActivity;
import pl.temomuko.autostoprace.ui.login.LoginActivity;
import pl.temomuko.autostoprace.util.rx.RxCacheHelper;

/**
 * Created by Szymon Kozak on 2016-03-19.
 */
public class ResetPassActivity extends BaseActivity implements ResetPassMvpView {

    private static final String TAG = ResetPassActivity.class.getSimpleName();
    private static final String BUNDLE_IS_PROGRESS_DIALOG_SHOWN = "bundle_is_progress_dialog_shown";

    @Inject ResetPassPresenter mResetPassPresenter;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.til_email) TextInputLayout mEmailTextInputLayout;
    @BindView(R.id.et_email) EditText mEmailEditText;
    @BindView(R.id.btn_reset) Button mResetButton;
    @BindView(R.id.tv_progress_info) TextView mProgressInfoTextView;
    @BindView(R.id.mpb_progress_info) MaterialProgressBar mProgressCircle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        getActivityComponent().inject(this);
        mResetPassPresenter.setupRxCacheHelper(this, RxCacheHelper.get(TAG));
        mResetPassPresenter.attachView(this);
        setupToolbarWithBack();
        setupEmailTextView();
        setListeners();
    }

    private void setListeners() {
        mResetButton.setOnClickListener(v -> {
            String email = mEmailEditText.getText().toString();
            mResetPassPresenter.resetPassword(email);
        });
    }

    private void setupEmailTextView() {
        String email = getIntent().getStringExtra(LoginActivity.EXTRA_EMAIL);
        if (email != null) {
            mEmailEditText.setText(email);
            mEmailEditText.setSelection(email.length());
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
        mProgressInfoTextView.setText(state ?
                getString(R.string.msg_processing_request) :
                getString(R.string.input_email_for_reset));
        mResetButton.setEnabled(!state);
        mResetButton.setActivated(!state);
        mProgressCircle.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    public void setInvalidEmailValidationError(boolean state) {
        mEmailTextInputLayout.setErrorEnabled(state);
        mEmailTextInputLayout.setError(state ? getString(R.string.error_invalid_email) : null);
    }
}
