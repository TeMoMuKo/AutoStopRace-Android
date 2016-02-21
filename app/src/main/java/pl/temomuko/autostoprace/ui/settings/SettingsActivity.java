package pl.temomuko.autostoprace.ui.settings;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.BaseActivity;

/**
 * Created by Szymon Kozak on 2016-02-04.
 */
public class SettingsActivity extends BaseActivity {

    private static final String TAG_SETTINGS_FRAGMENT = "settings_fragment";
    @Bind(R.id.toolbar) Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupToolbarWithBack();
        setupSettingsFragment(savedInstanceState);
    }

    private void setupToolbarWithBack() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupSettingsFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new SettingsFragment(), TAG_SETTINGS_FRAGMENT)
                    .commit();
        }
    }
}
