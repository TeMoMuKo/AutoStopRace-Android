package pl.temomuko.autostoprace.ui.post;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.Bind;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.BaseActivity;
import pl.temomuko.autostoprace.ui.main.MainActivity;

/**
 * Created by szymen on 2016-01-30.
 */
public class PostActivity extends BaseActivity implements PostMvpView {

    @Inject PostPresenter mPostPresenter;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.btn_send_location) Button mSendButton;
    @Bind(R.id.btn_cancel_send_location) Button mCancelButton;
    @Bind(R.id.et_message) EditText mMessageEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        getActivityComponent().inject(this);
        mPostPresenter.attachView(this);
        setupToolbar();
        setListeners();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setListeners() {
        mSendButton.setOnClickListener(v -> {
            String message = mMessageEditText.getText().toString();
            mPostPresenter.saveLocation(message);
        });
        mCancelButton.setOnClickListener(v -> mPostPresenter.backToMain());
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSuccessInfo() {
        Toast.makeText(this, R.string.msg_sent_location, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
