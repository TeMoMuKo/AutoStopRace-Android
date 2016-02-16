package pl.temomuko.autostoprace.injection.component;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.injection.AppContext;
import pl.temomuko.autostoprace.injection.module.ApplicationModule;
import pl.temomuko.autostoprace.util.ErrorHandler;

/**
 * Created by szymen on 2016-01-06.
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    @AppContext
    Context context();

    Application application();

    DataManager dataManager();

    ErrorHandler errorHandler();
}
