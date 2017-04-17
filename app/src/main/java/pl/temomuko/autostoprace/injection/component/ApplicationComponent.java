package pl.temomuko.autostoprace.injection.component;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import pl.temomuko.autostoprace.AsrApplication;
import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.remote.ErrorHandler;
import pl.temomuko.autostoprace.injection.AppContext;
import pl.temomuko.autostoprace.injection.module.ApplicationModule;
import pl.temomuko.autostoprace.service.LocationSyncService;

/**
 * Created by Szymon Kozak on 2016-01-06.
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(LocationSyncService locationSyncService);

    @AppContext
    Context context();

    Application application();

    DataManager dataManager();

    ErrorHandler errorHandler();

    void inject(AsrApplication asrApplication);
}