package pl.temomuko.autostoprace.ui.login.resetpass;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.BaseActivity;

/**
 * Created by Szymon Kozak on 2016-03-19.
 */
public class ResetPassActivity extends BaseActivity {

    @Bind(R.id.toolbar) Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        setupToolbarWithBack();
    }

    private void setupToolbarWithBack() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
