package pl.temomuko.autostoprace.ui.post;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;

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
    @Bind(R.id.et_message) EditText mMessageEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        getActivityComponent().inject(this);
        mPostPresenter.attachView(this);
        setupToolbarWithBack();
        setListeners();
    }

    @Override
    protected void onDestroy() {
        mPostPresenter.detachView();
        super.onDestroy();
    }

    private void setupToolbarWithBack() {
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
    }

    @Override
    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
