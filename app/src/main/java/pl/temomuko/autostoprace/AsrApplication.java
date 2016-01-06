package pl.temomuko.autostoprace;

import android.app.Application;
import android.content.Context;

import pl.temomuko.autostoprace.injection.component.ApplicationComponent;
import pl.temomuko.autostoprace.injection.component.DaggerApplicationComponent;
import pl.temomuko.autostoprace.injection.module.ApplicationModule;

/**
 * Created by szymen on 2016-01-06.
 */
public class AsrApplication extends Application {

    ApplicationComponent mApplicationComponent;

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

