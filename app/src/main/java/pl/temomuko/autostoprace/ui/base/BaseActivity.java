package pl.temomuko.autostoprace.ui.base;

import android.content.pm.ShortcutManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;
import pl.temomuko.autostoprace.AsrApplication;
import pl.temomuko.autostoprace.injection.component.ActivityComponent;
import pl.temomuko.autostoprace.injection.component.DaggerActivityComponent;
import pl.temomuko.autostoprace.injection.module.ActivityModule;
import pl.temomuko.autostoprace.ui.main.Shortcuts;

/**
 * Created by Szymon Kozak on 2016-01-06.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Inject Shortcuts shortcuts;

    private ActivityComponent mActivityComponent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    public ActivityComponent getActivityComponent() {
        if (mActivityComponent == null) {
            mActivityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(this))
                    .applicationComponent(AsrApplication.getApplicationComponent(this))
                    .build();
        }
        return mActivityComponent;
    }

    protected void reportShortcutUsage(String shortcutId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            getSystemService(ShortcutManager.class).reportShortcutUsed(shortcutId);
        }
    }
}
