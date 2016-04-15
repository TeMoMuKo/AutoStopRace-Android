package pl.temomuko.autostoprace.ui.base;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import pl.temomuko.autostoprace.AsrApplication;
import pl.temomuko.autostoprace.BuildConfig;
import pl.temomuko.autostoprace.injection.component.ActivityComponent;
import pl.temomuko.autostoprace.injection.component.DaggerActivityComponent;
import pl.temomuko.autostoprace.injection.module.ActivityModule;

/**
 * Created by Szymon Kozak on 2016-01-06.
 */
public abstract class BaseActivity extends AppCompatActivity {

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
                    .applicationComponent(AsrApplication.get(this).getComponent())
                    .build();
        }
        return mActivityComponent;
    }
}
