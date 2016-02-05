package pl.temomuko.autostoprace.ui.settings;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import javax.inject.Inject;

import butterknife.Bind;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.BaseActivity;

/**
 * Created by szymen on 2016-02-04.
 */
public class SettingsActivity extends BaseActivity implements SettingsMvpView {

    @Inject SettingsPresenter mSettingsPresenter;
    @Bind(R.id.toolbar) Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getActivityComponent().inject(this);
        mSettingsPresenter.attachView(this);
        setupToolbarWithBack();
    }

    @Override
    protected void onDestroy() {
        mSettingsPresenter.detachView();
        super.onDestroy();
    }

    private void setupToolbarWithBack() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
