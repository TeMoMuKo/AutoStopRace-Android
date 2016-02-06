package pl.temomuko.autostoprace.ui.about;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.BaseActivity;

/**
 * Created by szymen on 2016-02-05.
 */
public class AboutActivity extends BaseActivity {

    @Bind(R.id.toolbar) Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setupToolbarWithBack();
    }

    private void setupToolbarWithBack() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}