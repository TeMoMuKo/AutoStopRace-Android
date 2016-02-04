package pl.temomuko.autostoprace.ui.post;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;

import javax.inject.Inject;

import butterknife.Bind;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.BaseActivity;

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

    @Override
    protected void onDestroy() {
        mPostPresenter.detachView();
        super.onDestroy();
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
}
