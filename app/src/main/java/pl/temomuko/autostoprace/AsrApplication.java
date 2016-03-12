package pl.temomuko.autostoprace;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;
import pl.temomuko.autostoprace.injection.component.ApplicationComponent;
import pl.temomuko.autostoprace.injection.component.DaggerApplicationComponent;
import pl.temomuko.autostoprace.injection.module.ApplicationModule;

/**
 * Created by Szymon Kozak on 2016-01-06.
 */
public class AsrApplication extends Application {

    ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Locale.setDefault(new Locale(Constants.DEFAULT_LOCALE));
    }

    public ApplicationComponent getComponent() {
        if (mApplicationComponent == null) {
            mApplicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }
        return mApplicationComponent;
    }

    public static AsrApplication get(Context context) {
        return (AsrApplication) context.getApplicationContext();
    }
}

